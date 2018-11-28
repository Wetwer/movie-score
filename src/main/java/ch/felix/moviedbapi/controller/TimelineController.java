package ch.felix.moviedbapi.controller;

import ch.felix.moviedbapi.data.dao.ListMovieDao;
import ch.felix.moviedbapi.data.dao.MovieDao;
import ch.felix.moviedbapi.data.dao.TimeLineDao;
import ch.felix.moviedbapi.data.entity.ListMovie;
import ch.felix.moviedbapi.data.entity.Timeline;
import ch.felix.moviedbapi.data.entity.User;
import ch.felix.moviedbapi.service.ActivityService;
import ch.felix.moviedbapi.service.ListService;
import ch.felix.moviedbapi.service.auth.UserAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("timeline")
public class TimelineController {

    private MovieDao movieDao;
    private TimeLineDao timeLineDao;
    private ListMovieDao listMovieDao;

    private UserAuthService userAuthService;
    private ActivityService activityService;
    private ListService listService;

    public TimelineController(MovieDao movieDao, TimeLineDao timeLineDao, ListMovieDao listMovieDao,
                              UserAuthService userAuthService, ActivityService activityService,
                              ListService listService) {
        this.movieDao = movieDao;
        this.timeLineDao = timeLineDao;
        this.listMovieDao = listMovieDao;
        this.userAuthService = userAuthService;
        this.activityService = activityService;
        this.listService = listService;
    }

    @GetMapping("edit/{timelineId}")
    public String editTimeline(@PathVariable("timelineId") Long timeLineId,
                               Model model, HttpServletRequest request) {

        if (userAuthService.isUser(model, request)) {
            Timeline timeLine = timeLineDao.getById(timeLineId);
            if (userAuthService.isCurrentUser(request, timeLine.getUser())
                    || userAuthService.isAdministrator(request)) {
                model.addAttribute("timeline", timeLine);
                model.addAttribute("movies", movieDao.getOrderByTitle());
                listService.getNextListPlace(model, timeLine);

                model.addAttribute("page", "editTimeline");
                return "template";
            }
        }
        return "redirect:/list";
    }

    @PostMapping("edit/{timelineId}")
    public String saveNewMovie(@PathVariable("timelineId") Long timeLineId,
                               @RequestParam("place") String place,
                               @RequestParam("movie") Long movieId,
                               HttpServletRequest request) {
        if (userAuthService.isUser(request)) {
            Timeline timeline = timeLineDao.getById(timeLineId);
            if (userAuthService.isCurrentUser(request, timeline.getUser())
                    || userAuthService.isAdministrator(request)) {
                ListMovie listMovie = new ListMovie();
                listMovie.setPlace(Integer.valueOf(place));
                listMovie.setMovie(movieDao.getById(movieId));
                listMovie.setTimeline(timeline);
                listMovieDao.save(listMovie);
                return "redirect:/timeline/edit/" + timeLineId;
            }
        }
        return "redirect:/" + timeLineId;
    }

    @PostMapping("editatt/{timelineId}")
    public String editListAttributes(@PathVariable("timelineId") Long timeLineId,
                                     @RequestParam("title") String title,
                                     @RequestParam("description") String description,
                                     HttpServletRequest request) {
        if (userAuthService.isUser(request)) {
            Timeline timeline = timeLineDao.getById(timeLineId);
            if (userAuthService.isCurrentUser(request, timeline.getUser())
                    || userAuthService.isAdministrator(request)) {
                timeline.setTitle(title);
                timeline.setDescription(description);
                timeLineDao.save(timeline);
                return "redirect:/timeline/edit/" + timeLineId;
            }
        }
        return "redirect:/" + timeLineId;
    }

    @PostMapping("delete/movie/{movieId}")
    public String deleteFromList(@PathVariable("movieId") Long movieId, HttpServletRequest request) {
        if (userAuthService.isUser(request)) {
            ListMovie listMovie = listMovieDao.getById(movieId);
            if (userAuthService.isCurrentUser(request, listMovie.getTimeline().getUser())
                    || userAuthService.isAdministrator(request)) {
                listMovieDao.delete(listMovie);
                return "redirect:/timeline/edit/" + listMovie.getTimeline().getId();
            }
        }
        return "redirect:/list";
    }

    @GetMapping("new")
    public String getCreateForm(HttpServletRequest request, Model model) {
        if (userAuthService.isUser(model, request)) {
            model.addAttribute("page", "createTimeline");
            return "template";
        } else {
            return "redirect:/list";
        }
    }


    @PostMapping("new")
    public String createList(@RequestParam("title") String title,
                             @RequestParam("description") String description,
                             HttpServletRequest request) {
        if (userAuthService.isUser(request)) {
            User user = userAuthService.getUser(request).getUser();

            Timeline timeline = new Timeline();
            timeline.setTitle(title);
            timeline.setUser(user);
            timeline.setDescription(description);
            timeLineDao.save(timeline);

            activityService.log(user.getName() + " created list " + title, user);
            return "redirect:/list?list";
        } else {
            return "redirect:/list";
        }
    }

    @PostMapping("delete/{timelineId}")
    public String deleteTimeline(@PathVariable("timelineId") Long timeLineId,
                                 Model model, HttpServletRequest request) {
        if (userAuthService.isUser(model, request)) {
            Timeline timeline = timeLineDao.getById(timeLineId);
            if (userAuthService.isCurrentUser(request, timeline.getUser())
                    || userAuthService.isAdministrator(request)) {
                timeLineDao.delete(timeline);
                return "redirect:/list?deleted";
            }
        }
        return "redirect:/list/" + timeLineId + "?notdeleted";
    }
}

package ch.wetwer.moviedbapi.controller;

import ch.wetwer.moviedbapi.data.announcement.AnnouncementDao;
import ch.wetwer.moviedbapi.data.movie.Movie;
import ch.wetwer.moviedbapi.data.movie.MovieDao;
import ch.wetwer.moviedbapi.data.user.User;
import ch.wetwer.moviedbapi.service.GenreSearchType;
import ch.wetwer.moviedbapi.service.SearchService;
import ch.wetwer.moviedbapi.service.auth.UserAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wetwer
 * @project movie-score
 */
@Controller
@RequestMapping("")
public class HomeController {

    private SearchService searchService;
    private UserAuthService userAuthService;

    private AnnouncementDao announcementDao;
    private MovieDao movieDao;

    public HomeController(SearchService searchService, UserAuthService userAuthService,
                          AnnouncementDao announcementDao, MovieDao movieDao) {
        this.searchService = searchService;
        this.userAuthService = userAuthService;
        this.announcementDao = announcementDao;
        this.movieDao = movieDao;
    }

    @GetMapping
    public String getDashboard(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                               @RequestParam(name = "orderBy", required = false, defaultValue = "") String orderBy,
                               @RequestParam(name = "genre", required = false, defaultValue = "") String genreParam,
                               Model model, HttpServletRequest request) {
        if (userAuthService.isUser(model, request)) {
            User user = userAuthService.getUser(request).getUser();
            userAuthService.log(this.getClass(), user);

            if (!genreParam.equals("")) {
                return "redirect:/movies/1?search=" + search + "&orderBy=" + orderBy + "&genre=" + genreParam;
            }

            try {
                if (search.equals("") && orderBy.equals("")) {
                    model.addAttribute("startedVideos", searchService.findStartedVideos(user));
                } else {
                    model.addAttribute("startedVideos", new ArrayList<>());
                }
            } catch (NullPointerException e) {
                model.addAttribute("startedVideos", new ArrayList<>());
            }


            model.addAttribute("genres", searchService.getGenres(GenreSearchType.MOVIE));

            List<Movie> movieList = searchService.searchMoviesTop(search, orderBy);
            model.addAttribute("movies", movieList);
            model.addAttribute("series", searchService.searchSerieTop(search));
            model.addAttribute("announcements", announcementDao.getAll());
            model.addAttribute("recommended", movieDao.getRecommended());
            model.addAttribute("all", searchService.searchMovies("","", ""));


            model.addAttribute("search", search);
            model.addAttribute("orderBy", orderBy);
            model.addAttribute("currentGenre", genreParam);

            model.addAttribute("page", "home");
            return "template";
        } else {
            return "redirect:/login";
        }
    }
}

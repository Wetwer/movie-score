package ch.felix.moviedbapi.controller;

import ch.felix.moviedbapi.data.entity.User;
import ch.felix.moviedbapi.data.repository.RequestRepository;
import ch.felix.moviedbapi.data.repository.UserRepository;
import ch.felix.moviedbapi.service.CookieService;
import ch.felix.moviedbapi.service.SearchService;
import ch.felix.moviedbapi.service.ShaService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Felix
 * @date 24.05.2018
 * <p>
 * Project: movie-db-api
 * Package: ch.felix.moviedbapi.controller
 **/

@Controller
@RequestMapping("user")
public class UserController {

    private UserRepository userRepository;
    private RequestRepository requestRepository;

    private ShaService shaService;
    private CookieService cookieService;
    private SearchService searchService;


    public UserController(UserRepository userRepository, RequestRepository requestRepository, ShaService shaService,
                          CookieService cookieService, SearchService searchService) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.shaService = shaService;
        this.cookieService = cookieService;
        this.searchService = searchService;
    }

    @GetMapping(produces = "application/json")
    public String getUserList(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                              Model model, HttpServletRequest request) {
        try {
            model.addAttribute("currentUser", cookieService.getCurrentUser(request));
        } catch (NullPointerException e) {
            return "redirect:/login?redirect=/user";
        }

        model.addAttribute("users", searchService.searchUser(search));

        model.addAttribute("search", search);

        model.addAttribute("page", "userList");
        return "template";
    }

    @GetMapping(value = "{userId}", produces = "application/json")
    public String getOneUser(@PathVariable("userId") String userId, Model model, HttpServletRequest request) {
        try {
            model.addAttribute("currentUser", cookieService.getCurrentUser(request));
        } catch (NullPointerException e) {
            return "redirect:/login?redirect=/user/" + userId;
        }

        User user = userRepository.findUserById(Long.valueOf(userId));

        model.addAttribute("user", userRepository.findUserById(Long.valueOf(userId)));
        model.addAttribute("requests", requestRepository.findAllByUser(user));
        model.addAttribute("page", "user");
        return "template";
    }

    @PostMapping(value = "{userId}/role/{role}", produces = "application/json")
    public String setRole(@PathVariable("userId") String userId, @PathVariable("role") String role) {
        try {
            User user = userRepository.findUserById(Long.valueOf(userId));
            user.setRole(Integer.valueOf(role));
            userRepository.save(user);
            return "redirect:/user/" + userId + "?role";
        } catch (NumberFormatException e) {
            return "redirect:/user/" + userId + "?error";
        }
    }

    @PostMapping("{userId}/name")
    public String setUsername(@PathVariable("userId") String userId, @RequestParam("name") String newName) {
        try {
            User user = userRepository.findUserById(Long.valueOf(userId));
            user.setName(newName);
            userRepository.save(user);
            return "redirect:/user/" + userId + "?username";
        } catch (ConstraintViolationException | NumberFormatException e) {
            return "redirect:/user/" + userId;
        }


    }

    @PostMapping("{userId}/password")
    public String setPassword(@PathVariable("userId") String userId,
                              @RequestParam("old") String oldPassword,
                              @RequestParam("new") String newPassword) {
        try {
            User user = userRepository.findUserByIdAndPasswordSha(Long.valueOf(userId), oldPassword);
            user.setPasswordSha(shaService.encode(newPassword));
            userRepository.save(user);
            return "redirect:/user/" + userId + "?password";
        } catch (NullPointerException | ConstraintViolationException | NumberFormatException e) {
            return "redirect:/user/" + userId;
        }
    }

}

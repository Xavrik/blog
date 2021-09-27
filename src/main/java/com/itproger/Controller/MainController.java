package com.itproger.Controller;

import com.itproger.models.Review;
import com.itproger.models.Role;
import com.itproger.models.User;
import com.itproger.repo.ReviewRepository;
import com.itproger.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;


@Controller
public class MainController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", "World");
        return "home";

    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Страница про нас");
        return "about";

    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        Iterable<Review> reviews = reviewRepository.findAll();
        model.addAttribute("title", "Страница с отзывами");
        model.addAttribute("reviews", reviews);
        return "reviews";

    }

    @PostMapping("/reviews-add")
    public String reviewsAdd(@AuthenticationPrincipal User user, @RequestParam String title, @RequestParam String text, Model model) {
        Review review = new Review(title, text, user);
        reviewRepository.save(review);

        return "redirect:/reviews";
    }

    @GetMapping("/reviews/{id}")
    public String reviewInfo(@PathVariable(value = "id") long reviewId, Model model) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        ArrayList<Review> result = new ArrayList<>();
        review.ifPresent(result::add);
        model.addAttribute("review", result);
        return "review_info";

    }

    @GetMapping("/reviews/{id}/update")
    public String reviewUpdate(@PathVariable(value = "id") long reviewId, Model model) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        ArrayList<Review> result = new ArrayList<>();
        review.ifPresent(result::add);
        model.addAttribute("review", result);
        return "review_update";
    }

    @PostMapping("/reviews/{id}/update")
    public String reviewsUpdateForm(@PathVariable(value = "id") long reviewId, @RequestParam String title, @RequestParam String text,Model model) throws ClassNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ClassNotFoundException());
        review.setTitle(title);
        review.setText(text);
        reviewRepository.save(review);
        return "redirect:/reviews/" + reviewId;
    }

    @PostMapping("/reviews/{id}/delete")
    public String reviewsDelete(@PathVariable(value = "id") long reviewId,Model model) throws ClassNotFoundException {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ClassNotFoundException());
        reviewRepository.delete(review);

        return "redirect:/reviews";
    }

    @GetMapping("/reg")
    public String reg() {
        return "reg";
    }

    @PostMapping("/reg")
    public String addUser(User user, Model model) {
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";

    }


    @RequestMapping(value="/user/", method = RequestMethod.GET)
        public String user (User userForm, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        userForm =userRepository.findByUsername(name);
        model.addAttribute("username", userForm.getUsername());
        model.addAttribute("email", userForm.getEmail());
        model.addAttribute("password",userForm.getPassword() );
        model.addAttribute("user", Role.USER);
        model.addAttribute("admin", Role.ADMIN);
        model.addAttribute("redactor", Role.REDACTOR);

        return "/user";
    }

@PostMapping("/user/")
public String Submit ( User userForm, Model model)  {
    User user = new User();
    user.setEnabled(true);
    user.setUsername(userForm.getUsername());
    user.setEmail(userForm.getEmail());
    user.setPassword(userForm.getPassword());
//    if( role.equals(Role.USER)){
//        user.setRoles(Collections.singleton(Role.USER));
//    }
//    else if( role.equals(Role.ADMIN)){
//        user.setRoles(Collections.singleton(Role.ADMIN));
//    }
//    else if( role.equals(Role.REDACTOR)){
//        user.setRoles(Collections.singleton(Role.REDACTOR));
//    }

    userRepository.save(user);

    return "redirect:/user/";
}



}



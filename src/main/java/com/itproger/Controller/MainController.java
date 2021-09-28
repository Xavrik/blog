package com.itproger.Controller;

import com.itproger.models.Review;
import com.itproger.models.Role;
import com.itproger.models.User;
import com.itproger.repo.ReviewRepository;
import com.itproger.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
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

    // Отслеживаем переход на страницу /user/
    // В функции получаем зарегистрированного пользователя
    @GetMapping("/user/")
    public String user(Principal principal, Model model) {
        // Находим пользвоателя по имени авторизованого пользователя
        User user = userRepository.findByUsername(principal.getName());

        // Передаем данные про авторизованого пользователя в шаблон
        // Там они будут выведены в форме
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("role", user.getRoles());
        return "user";
    }

    // Обработка данных из формы
    @PostMapping("/user/")
    public String updateUser(Principal principal, User userForm, Model model) {
        // Находим пользвоателя по имени авторизованого пользователя
        User user = userRepository.findByUsername(principal.getName());
        // Устанавливаем для этого пользователя новые данные
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setRoles(userForm.getRoles());

        // Сохраняем (обновляем) данные про пользователя
        userRepository.save(user);
        // Выполняем редирект
        return "redirect:/user/";
    }
    @GetMapping("/admin")
    public String adminPanel(User user, Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("us",users );

        return "/admin";
    }
    @GetMapping("/admin/{name}/{id}")
    public String adminInfo(@PathVariable(value = "name") String name,@PathVariable(value = "id") long id,  Model model) {

        User us = userRepository.findByUsername(name);

        Iterable<Review> rev = reviewRepository.findAll();
        model.addAttribute("us",rev);

        return "/admin_info";

    }



}



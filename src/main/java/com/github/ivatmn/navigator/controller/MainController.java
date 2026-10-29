package com.github.ivatmn.navigator.controller;

import com.github.ivatmn.navigator.entity.User;
import com.github.ivatmn.navigator.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final UserRepository userRepository;

    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        String login = authentication.getName();
        model.addAttribute("username", login);

        // Получаем пользователя из БД, чтобы узнать его офис
        User user = userRepository.findByLogin(login).orElse(null);
        if (user != null && user.getOffice() != null) {
            // Передаем название офиса в шаблон
            model.addAttribute("officeName", user.getOffice().getName());
        } else {
            model.addAttribute("officeName", "Офис не назначен");
        }

        return "index";
    }
}
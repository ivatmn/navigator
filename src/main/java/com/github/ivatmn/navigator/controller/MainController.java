package com.github.ivatmn.navigator.controller;

import com.github.ivatmn.navigator.entity.MenuItem;
import com.github.ivatmn.navigator.entity.User;
import com.github.ivatmn.navigator.repository.MenuItemRepository;
import com.github.ivatmn.navigator.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public MainController(UserRepository userRepository, MenuItemRepository menuItemRepository) {
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/")
    public String index(Authentication authentication,
                        Model model,
                        @RequestParam(value = "menuId", required = false) Long menuId) {

        String login = authentication.getName();
        User user = userRepository.findByLogin(login).orElse(null);

        if (user == null) return "redirect:/login";

        // 1. Инфо о пользователе
        model.addAttribute("username", user.getDisplayName());
        if (user.getOffice() != null) {
            model.addAttribute("officeName", user.getOffice().getName());
        }

        // 2. Получаем роли
        Set<String> roleCodes = user.getRoles().stream()
                .map(role -> role.getCode())
                .collect(Collectors.toSet());

        // 3. Получаем корневое меню
        List<MenuItem> rootMenu = menuItemRepository.findRootMenuByRoles(roleCodes);
        model.addAttribute("rootMenu", rootMenu);

        // 4. Определяем выбранный пункт (если не передан, берем первый)
        MenuItem selectedRoot = null;
        if (!rootMenu.isEmpty()) {
            if (menuId != null) {
                selectedRoot = rootMenu.stream()
                        .filter(m -> m.getId().equals(menuId))
                        .findFirst()
                        .orElse(rootMenu.get(0));
            } else {
                selectedRoot = rootMenu.get(0);
            }
        }
        model.addAttribute("selectedRoot", selectedRoot);

        // 5. Получаем подменю для выбранного пункта
        if (selectedRoot != null) {
            List<MenuItem> subMenu = menuItemRepository.findChildMenuByRoles(selectedRoot.getId(), roleCodes);
            model.addAttribute("subMenu", subMenu);
        }

        return "index";
    }
}
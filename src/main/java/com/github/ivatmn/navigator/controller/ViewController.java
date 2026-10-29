package com.github.ivatmn.navigator.controller;

import com.github.ivatmn.navigator.entity.ViewDefinition;
import com.github.ivatmn.navigator.repository.ViewDefinitionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class ViewController {

    private final ViewDefinitionRepository viewRepository;
    private final JdbcTemplate jdbcTemplate;

    public ViewController(ViewDefinitionRepository viewRepository, JdbcTemplate jdbcTemplate) {
        this.viewRepository = viewRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Меняем путь с {id} на {code}
    @GetMapping("/view/{code}")
    public String showView(@PathVariable String code, Model model) {
        ViewDefinition view = viewRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Представление не найдено: " + code));

        // Выполняем SQL из базы
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(view.getSql());

        model.addAttribute("viewName", view.getName());
        model.addAttribute("rows", rows);
        model.addAttribute("columns", rows.isEmpty() ? List.of() : rows.get(0).keySet());

        return "view";
    }
}
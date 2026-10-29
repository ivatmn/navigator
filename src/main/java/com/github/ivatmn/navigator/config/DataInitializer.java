package com.github.ivatmn.navigator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.ivatmn.navigator.entity.*;
import com.github.ivatmn.navigator.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      OfficeRepository officeRepository,
                                      RoleRepository roleRepository,
                                      MenuItemRepository menuItemRepository,
                                      ViewDefinitionRepository viewDefinitionRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Офис, Роль, Пользователь (как было)
            Office office = officeRepository.findAll().stream().findFirst().orElseGet(() -> {
                Office o = new Office(); o.setCode("00"); o.setName("Головной офис"); return officeRepository.save(o);
            });

            Role adminRole = roleRepository.findByCode("ADMIN").orElseGet(() -> {
                Role r = new Role(); r.setCode("ADMIN"); r.setName("Администратор"); return roleRepository.save(r);
            });

            if (userRepository.findByLogin("admin").isEmpty()) {
                User admin = new User();
                admin.setLogin("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setDisplayName("Администратор");
                admin.setOffice(office);
                admin.setRoles(Set.of(adminRole));
                userRepository.save(admin);
            }

            // 2. Чтение YAML
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            try (InputStream is = new ClassPathResource("menu-config.yml").getInputStream()) {
                MenuConfigDto config = mapper.readValue(is, MenuConfigDto.class);

                // 3. Сохраняем/обновляем представления
                Map<String, ViewDefinition> viewMap = new HashMap<>();
                if (config.getViews() != null) {
                    for (MenuConfigDto.ViewDto v : config.getViews()) {
                        ViewDefinition view = viewDefinitionRepository.findByCode(v.getCode())
                                .orElseGet(() -> new ViewDefinition());

                        view.setGuid(v.getGuid());
                        view.setName(v.getName());
                        view.setCode(v.getCode());
                        view.setSql(v.getSql());

                        viewDefinitionRepository.save(view);
                        viewMap.put(v.getCode(), view);
                    }
                }

                // 4. Сохраняем/обновляем МЕНЮ (с защитой от дублей)
                if (config.getMenu() != null) {
                    // Сначала создаем/обновляем корневые
                    for (MenuConfigDto.MenuItemDto m : config.getMenu()) {
                        if (m.getParentName() == null) {
                            MenuItem item = menuItemRepository.findByNameAndParentIsNull(m.getName())
                                    .orElseGet(() -> new MenuItem());

                            item.setName(m.getName());
                            item.setMenuType(m.getMenuType());
                            item.setIcon(m.getIcon());
                            item.setOrder(m.getOrder());
                            if (m.getViewCode() != null) {
                                item.setViewDefinition(viewMap.get(m.getViewCode()));
                            }
                            item.setRoles(Set.of(adminRole));

                            menuItemRepository.save(item);
                        }
                    }

                    // Затем создаем/обновляем дочерние
                    for (MenuConfigDto.MenuItemDto m : config.getMenu()) {
                        if (m.getParentName() != null) {
                            // Находим родителя по имени
                            Optional<MenuItem> parentOpt = menuItemRepository.findByNameAndParentIsNull(m.getParentName());
                            if (parentOpt.isEmpty()) {
                                continue; // Если родителя нет, пропускаем
                            }
                            MenuItem parent = parentOpt.get();

                            MenuItem item = menuItemRepository.findByNameAndParent(m.getName(), parent)
                                    .orElseGet(() -> new MenuItem());

                            item.setName(m.getName());
                            item.setMenuType(m.getMenuType());
                            item.setIcon(m.getIcon());
                            item.setOrder(m.getOrder());
                            item.setParent(parent); // Обязательно ставим родителя

                            if (m.getViewCode() != null) {
                                item.setViewDefinition(viewMap.get(m.getViewCode()));
                            }
                            item.setRoles(Set.of(adminRole));

                            menuItemRepository.save(item);
                        }
                    }
                }
            }
            System.out.println("✅ Данные из menu-config.yml загружены/обновлены");
        };
    }
}
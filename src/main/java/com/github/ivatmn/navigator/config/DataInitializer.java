package com.github.ivatmn.navigator.config;

import com.github.ivatmn.navigator.entity.Office;
import com.github.ivatmn.navigator.entity.Role;
import com.github.ivatmn.navigator.entity.User;
import com.github.ivatmn.navigator.repository.OfficeRepository;
import com.github.ivatmn.navigator.repository.RoleRepository;
import com.github.ivatmn.navigator.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      OfficeRepository officeRepository,
                                      RoleRepository roleRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByLogin("admin").isEmpty()) {
                // 1. Офис
                Office office = new Office();
                office.setCode("00");
                office.setName("Головной офис");
                office.setAddress("г. Москва, ул. Строителей, д. 1");
                office.setDescription("Головной офис компании");
                officeRepository.save(office); // Сохраняем через репозиторий

                // 2. Роль
                Role role = new Role();
                role.setCode("ADMIN");
                role.setName("Администратор");
                roleRepository.save(role); // Сохраняем через репозиторий

                // 3. Пользователь
                User admin = new User();
                admin.setLogin("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setDisplayName("Администратор");
                admin.setOffice(office);
                admin.setRoles(Set.of(role));

                userRepository.save(admin);
                System.out.println("✅ Тестовый пользователь создан: логин=admin, пароль=admin123");
            }
        };
    }
}
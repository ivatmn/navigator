package com.github.ivatmn.navigator.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к странице логина и статическим ресурсам (CSS, JS, WebJars)
                        .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**").permitAll()
                        // Все остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")              // GET-страница, которую мы создали
                        .loginProcessingUrl("/login")     // POST-обработчик, куда отправляется форма
                        .defaultSuccessUrl("/", true)     // Куда перенаправить после успеха
                        .failureUrl("/login?error")       // Ошибка при неверном логине/пароле
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
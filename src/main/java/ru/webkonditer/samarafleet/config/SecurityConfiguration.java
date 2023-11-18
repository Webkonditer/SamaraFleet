package ru.webkonditer.samarafleet.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    /**
     * Конфигурация безопасности для фильтрации HTTP-запросов.
     *
     * @param http Объект для настройки HTTP-безопасности.
     * @return SecurityFilterChain для обработки запросов согласно конфигурации безопасности.
     * @throws Exception В случае возникновения ошибок при настройке безопасности.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключение CSRF-защиты
                .csrf(AbstractHttpConfigurer::disable)
                // Настройка разрешений для HTTP-запросов
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/api/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                // Управление сессиями (в данном случае, без создания сессий)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Использование HTTP Basic аутентификации с настройками по умолчанию
                .httpBasic(Customizer.withDefaults());

        // Возвращаем SecurityFilterChain, построенный на основе настроек http
        return http.build();
    }
}

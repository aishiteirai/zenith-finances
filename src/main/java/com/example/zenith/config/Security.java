package com.example.zenith.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Security {

    /**
     * Define o algoritmo de hashing BCrypt para encriptar as senhas dos investidores.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração principal da cadeia de filtros de segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuração para o H2 Database Console
                // Necessário desativar CSRF para o console e permitir a renderização de frames
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // 2. Definição de permissões de rotas
                .authorizeHttpRequests(auth -> auth
                        // Rotas públicas: Landing Page, Login, Registo e H2 Console
                        .requestMatchers("/", "/login", "/register", "/error", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        // Qualquer outra rota do sistema Zenith exige autenticação
                        .anyRequest().authenticated()
                )

                // 3. Configuração do Formulário de Login personalizado (Interface Zenith)
                .formLogin(form -> form
                        .loginPage("/login") // Direciona para a sua página personalizada
                        .defaultSuccessUrl("/home", true) // Rota após sucesso no login
                        .permitAll()
                )

                // 4. Configuração de encerramento de sessão
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Parâmetro para exibir a mensagem de sucesso no front-end
                        .permitAll()
                );

        return http.build();
    }
}
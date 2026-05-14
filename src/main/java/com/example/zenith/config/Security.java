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

    // 1. Adicionado o Bean para criptografar as senhas no padrão BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 2. Adicionado o "/register" e o "/error" na lista de acessos permitidos
                        .requestMatchers("/login", "/register", "/error", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated() // Qualquer outra página exigirá login
                )
                .formLogin(form -> form
                        .loginPage("/login") // Indica ao Spring que o nosso formulário personalizado está na rota /login
                        .defaultSuccessUrl("/home", true) // Redireciona para /home caso o login seja feito com sucesso
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Mostra a mensagem verde quando terminar a sessão
                        .permitAll()
                );

        return http.build();
    }
}
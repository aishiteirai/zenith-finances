package com.example.zenith.config;

import com.example.zenith.model.Investidor;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private InvestidorRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Procura o investidor pelo e-mail (que é o nosso 'username' no ecrã Zenith)
        Investidor investidor = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado no sistema: " + email));

        // Constrói o utilizador para o Spring Security validar a sessão
        return User.builder()
                .username(investidor.getEmail())
                .password(investidor.getSenhaHash()) // A senha criptografada que está guardada no H2
                .roles("USER")
                .build();
    }
}
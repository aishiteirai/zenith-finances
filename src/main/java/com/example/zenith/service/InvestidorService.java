package com.example.zenith.service;

import com.example.zenith.dto.RegistroDTO;
import com.example.zenith.model.Investidor;
import com.example.zenith.repository.InvestidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvestidorService {

    @Autowired
    private InvestidorRepository investidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public boolean registrarNovoInvestidor(RegistroDTO dto) {
        // Verifica se o email já existe
        if (investidorRepository.findByEmail(dto.getEmail()).isPresent()) {
            return false;
        }

        // Mapeia o DTO para a Entidade
        Investidor novoInvestidor = new Investidor();
        novoInvestidor.setNome(dto.getNome());
        novoInvestidor.setEmail(dto.getEmail());
        novoInvestidor.setSenhaHash(passwordEncoder.encode(dto.getSenha()));

        investidorRepository.save(novoInvestidor);
        return true;
    }
}
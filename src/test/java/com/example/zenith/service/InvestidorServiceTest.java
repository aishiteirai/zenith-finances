package com.example.zenith.service;

import com.example.zenith.dto.RegistroDTO;
import com.example.zenith.model.Investidor;
import com.example.zenith.repository.InvestidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestidorServiceTest {

    @Mock
    private InvestidorRepository investidorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InvestidorService investidorService;

    private RegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        registroDTO = new RegistroDTO();
        registroDTO.setNome("John Doe");
        registroDTO.setEmail("investor@zenith.node");
        registroDTO.setSenha("SenhaForte1234");
    }

    @Test
    void deveRegistrarNovoInvestidorComSucesso() {
        when(investidorRepository.findByEmail(registroDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registroDTO.getSenha())).thenReturn("hashCriptografado");

        boolean sucesso = investidorService.registrarNovoInvestidor(registroDTO);

        assertTrue(sucesso);
        verify(investidorRepository, times(1)).save(any(Investidor.class));
    }

    @Test
    void naoDeveRegistrarQuandoEmailJaExiste() {
        when(investidorRepository.findByEmail(registroDTO.getEmail())).thenReturn(Optional.of(new Investidor()));

        boolean sucesso = investidorService.registrarNovoInvestidor(registroDTO);

        assertFalse(sucesso);
        verify(investidorRepository, never()).save(any(Investidor.class));
    }
}
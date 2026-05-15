package com.example.zenith.controller;

import com.example.zenith.service.InvestidorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignora temporariamente os filtros de segurança para testar o controller isolado
class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvestidorService investidorService;

    @Test
    void deveExibirFormularioDeRegistro() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registroDTO"));
    }

    @Test
    void deveRetornarErroDeValidacaoSeSenhaForCurta() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "John")
                        .param("email", "john@zenith.node")
                        .param("senha", "123")) // Senha inválida (< 12 caracteres)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void deveRedirecionarParaLoginAposRegistroComSucesso() throws Exception {
        // Simula que o serviço efetuou o registo com sucesso
        when(investidorService.registrarNovoInvestidor(any())).thenReturn(true);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "Satoshi")
                        .param("email", "satoshi@zenith.node")
                        .param("senha", "SegurancaAbsoluta123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }
}
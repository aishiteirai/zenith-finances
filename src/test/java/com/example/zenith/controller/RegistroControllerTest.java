package com.example.zenith.controller;

import com.example.zenith.dto.RegistroDTO;
import com.example.zenith.service.InvestidorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroController.class)
@AutoConfigureMockMvc(addFilters = false) //desativando a segurança do spring para o test de controller
class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvestidorService investidorService;

    @Test
    void deveExibirTelaDeRegistroComDTOPreenchido() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registroDTO"));
    }

    @Test
    void deveRegistrarComSucessoERedirecionarParaLogin() throws Exception {
        when(investidorService.registrarNovoInvestidor(any(RegistroDTO.class))).thenReturn(true);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "Ada Lovelace")
                        .param("email", "ada@zenith.node")
                        .param("senha", "SenhaSuperForte123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(investidorService, times(1)).registrarNovoInvestidor(any(RegistroDTO.class));
    }

    @Test
    void deveRetornarErroDeValidacaoSeNomeForVazio() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "")
                        .param("email", "ada@zenith.node")
                        .param("senha", "SenhaSuperForte123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registroDTO", "nome"));

        verify(investidorService, never()).registrarNovoInvestidor(any());
    }

    @Test
    void deveRetornarErroDeValidacaoSeEmailForInvalido() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "Ada Lovelace")
                        .param("email", "email-sem-arroba")
                        .param("senha", "SenhaSuperForte123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registroDTO", "email"));

        verify(investidorService, never()).registrarNovoInvestidor(any());
    }

    @Test
    void deveRetornarErroDeValidacaoSeSenhaForCurta() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "Ada Lovelace")
                        .param("email", "ada@zenith.node")
                        .param("senha", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registroDTO", "senha"));

        verify(investidorService, never()).registrarNovoInvestidor(any());
    }

    @Test
    void deveRetornarErroNaTelaSeEmailJaEstiverEmUso() throws Exception {
        when(investidorService.registrarNovoInvestidor(any(RegistroDTO.class))).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nome", "Hacker")
                        .param("email", "admin@zenith.node")
                        .param("senha", "SenhaSuperForte123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attribute("erro", true));

        verify(investidorService, times(1)).registrarNovoInvestidor(any(RegistroDTO.class));
    }
}
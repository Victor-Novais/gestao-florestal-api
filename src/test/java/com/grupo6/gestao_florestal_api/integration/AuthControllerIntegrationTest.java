package com.grupo6.gestao_florestal_api.integration;

import com.grupo6.gestao_florestal_api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void devePermitirLoginComEmail() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "%s",
                                  "password": "%s"
                                }
                                """.formatted(adminEmail, adminPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(adminUsername))
                .andExpect(jsonPath("$.email").value(adminEmail))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));
    }

    @Test
    void deveCadastrarUsuarioComEmailUnico() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "novo.usuario",
                                  "email": "novo.usuario@florestal.com",
                                  "password": "Senha@123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("novo.usuario"))
                .andExpect(jsonPath("$.email").value("novo.usuario@florestal.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_COLABORADOR"));
    }

    @Test
    void deveRejeitarCadastroComEmailDuplicado() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "outro.usuario",
                                  "email": "%s",
                                  "password": "Senha@123"
                                }
                                """.formatted(adminEmail)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Email ja cadastrado"));
    }

    @Test
    void deveValidarFormatoDoEmailNoCadastro() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "usuario.invalido",
                                  "email": "email-invalido",
                                  "password": "Senha@123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void deveRetornarEmailNoEndpointMe() throws Exception {
        String token = login(adminEmail, adminPassword);

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(adminUsername))
                .andExpect(jsonPath("$.email").value(adminEmail))
                .andExpect(jsonPath("$.roles", containsInAnyOrder("ROLE_ADMIN")));
    }
}

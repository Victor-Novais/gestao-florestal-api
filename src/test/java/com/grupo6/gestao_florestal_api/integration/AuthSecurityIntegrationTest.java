package com.grupo6.gestao_florestal_api.integration;

import com.grupo6.gestao_florestal_api.support.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthSecurityIntegrationTest extends AbstractIntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "/auth/me",
            "/api/areas",
            "/api/colaboradores",
            "/api/equipamentos",
            "/api/especies",
            "/api/inventarios",
            "/api/ocorrencias",
            "/api/plantios",
            "/api/relatorios/equipamentos/inventario",
            "/api/relatorios/especies/alertas-ameacadas",
            "/api/relatorios/inventarios/historico-parcela?areaId=00000000-0000-0000-0000-000000000000&parcela=P1",
            "/api/relatorios/ocorrencias/consolidado",
            "/api/relatorios/plantio/metas",
            "/api/relatorios/produtividade"
    })
    void deveRetornar401SemToken(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/areas",
            "/api/colaboradores",
            "/api/equipamentos",
            "/api/especies",
            "/api/inventarios",
            "/api/ocorrencias",
            "/api/plantios"
    })
    void deveRetornar401SemTokenParaPost(String endpoint) throws Exception {
        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}

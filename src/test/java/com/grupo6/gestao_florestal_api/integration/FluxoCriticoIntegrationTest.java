package com.grupo6.gestao_florestal_api.integration;

import com.grupo6.gestao_florestal_api.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FluxoCriticoIntegrationTest extends AbstractIntegrationTest {

    @Test
    void deveExecutarFluxoAutenticacaoPlantioInventarioOcorrenciaERelatorios() throws Exception {
        String adminToken = login(adminUsername, adminPassword);
        String colaboradorToken = login(colaboradorUsername, colaboradorPassword);

        String areaResponse = mockMvc.perform(post("/api/areas")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "identificadorUnico": "AREA-IT-001",
                                  "nome": "Área Integração",
                                  "latitude": -12.9714,
                                  "longitude": -38.5014,
                                  "municipio": "Salvador",
                                  "estado": "BA",
                                  "hectares": 120.50,
                                  "tipoFloresta": "NATIVA",
                                  "bioma": "MATA_ATLANTICA",
                                  "status": "ATIVA"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String areaId = objectMapper.readTree(areaResponse).get("id").asText();

        String especieResponse = mockMvc.perform(post("/api/especies")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeCientifico": "Myracrodruon urundeuva",
                                  "nomePopular": "Aroeira",
                                  "familiaBotanica": "Anacardiaceae",
                                  "porte": "ARBOREO",
                                  "statusConservacao": "POUCO_PREOCUPANTE",
                                  "cicloVidaAnos": 30,
                                  "exigenciasClimaticas": "Tropical",
                                  "exigenciasSolo": "Bem drenado",
                                  "nativa": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String especieId = objectMapper.readTree(especieResponse).get("id").asText();

        mockMvc.perform(post("/api/plantios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "dataHora": "2026-04-20T10:00:00",
                                  "areaFlorestalId": "%s",
                                  "especieVegetalId": "%s",
                                  "colaboradorId": "%s",
                                  "quantidadeMudas": 150,
                                  "latitudeTalhao": -12.9700,
                                  "longitudeTalhao": -38.5000,
                                  "temperatura": 28.5,
                                  "umidade": 70.0,
                                  "chuva": false,
                                  "metodoPlantio": "SEMEADURA_DIRETA",
                                  "observacoes": "Plantio de integração"
                                }
                                """.formatted(areaId, especieId, colaboradorId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.colaboradorId").value(colaboradorId.toString()));

        mockMvc.perform(post("/api/inventarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + colaboradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "numeroParcela": "P1",
                                  "areaFlorestalId": "%s",
                                  "dataVistoria": "2026-04-21",
                                  "presencaPragas": false,
                                  "descricaoPragas": null,
                                  "estadoGeral": "BOM",
                                  "especies": [
                                    {
                                      "especieId": "%s",
                                      "quantidade": 100,
                                      "dapMedio": 12.50,
                                      "alturaMedia": 4.20
                                    }
                                  ]
                                }
                                """.formatted(areaId, especieId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.colaboradorId").value(colaboradorId.toString()));

        mockMvc.perform(post("/api/ocorrencias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + colaboradorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tipo": "PRAGA_DOENCA",
                                  "areaFlorestalId": "%s",
                                  "latitude": -12.9690,
                                  "longitude": -38.4990,
                                  "urgencia": "ALTO",
                                  "descricao": "Ocorrência de teste",
                                  "urlFotos": ["https://example.com/foto.jpg"]
                                }
                                """.formatted(areaId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.colaboradorId").value(colaboradorId.toString()));

        mockMvc.perform(get("/api/plantios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("areaId", areaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/inventarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("area", areaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/ocorrencias")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("area", areaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/relatorios/inventarios/historico-parcela")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("areaId", areaId)
                        .param("parcela", "P1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/relatorios/ocorrencias/consolidado")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("dataInicio", "2026-04-01")
                        .param("dataFim", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens", notNullValue()));

        mockMvc.perform(get("/api/relatorios/produtividade")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .param("dataInicio", "2026-04-01")
                        .param("dataFim", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMudasPorArea", notNullValue()))
                .andExpect(jsonPath("$.coberturaPorBioma", notNullValue()))
                .andExpect(jsonPath("$.ocorrenciasPorTipoUrgencia", notNullValue()))
                .andExpect(jsonPath("$.indiceSaudeFlorestal.mediaScore", notNullValue()));
    }
}

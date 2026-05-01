package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.OcorrenciaConsolidadoResponseDTO;
import com.grupo6.gestao_florestal_api.service.RelatorioOcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios/ocorrencias")
@RequiredArgsConstructor
@Tag(name = "Relatorios de Ocorrencias", description = "Relatórios consolidados de ocorrências")
public class RelatorioOcorrenciaController {

    private final RelatorioOcorrenciaService relatorioOcorrenciaService;

    @GetMapping("/consolidado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter consolidado de ocorrências")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consolidado retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<OcorrenciaConsolidadoResponseDTO> buscarConsolidado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(relatorioOcorrenciaService.buscarConsolidado(dataInicio, dataFim));
    }
}

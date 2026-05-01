package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.ProdutividadeResponseDTO;
import com.grupo6.gestao_florestal_api.service.RelatorioProdutividadeService;
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
@RequestMapping("/api/relatorios/produtividade")
@RequiredArgsConstructor
@Tag(name = "Relatorios de Produtividade", description = "Indicadores consolidados de produtividade florestal")
public class RelatorioProdutividadeController {

    private final RelatorioProdutividadeService relatorioProdutividadeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obter indicadores de produtividade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Indicadores retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ProdutividadeResponseDTO> buscarIndicadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(relatorioProdutividadeService.buscarIndicadores(dataInicio, dataFim));
    }
}

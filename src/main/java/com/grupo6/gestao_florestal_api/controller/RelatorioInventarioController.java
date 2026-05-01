package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.HistoricoParcelaItemDTO;
import com.grupo6.gestao_florestal_api.service.RelatorioInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios/inventarios")
@RequiredArgsConstructor
public class RelatorioInventarioController {

    private final RelatorioInventarioService relatorioInventarioService;

    @GetMapping("/historico-parcela")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<List<HistoricoParcelaItemDTO>> buscarHistoricoParcela(
            @RequestParam UUID areaId,
            @RequestParam String parcela,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                relatorioInventarioService.buscarHistoricoParcela(areaId, parcela, dataInicio, dataFim, auth)
        );
    }
}

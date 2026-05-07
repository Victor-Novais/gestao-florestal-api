package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.InventarioEquipamentoDTO;
import com.grupo6.gestao_florestal_api.dto.PrevisaoReposicaoDTO;
import com.grupo6.gestao_florestal_api.service.RelatorioEquipamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/equipamentos")
@RequiredArgsConstructor
public class RelatorioEquipamentoController {

    private final RelatorioEquipamentoService relatorioService;

    // Inventário completo com statusEstoque: OK, BAIXO ou CRITICO
    @GetMapping("/inventario")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventarioEquipamentoDTO>> inventario() {
        return ResponseEntity.ok(relatorioService.inventario());
    }

    // Itens com previsão de vencimento nos próximos N dias (default 30), ordenados por diasRestantes ASC
    @GetMapping("/previsao-reposicao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrevisaoReposicaoDTO>> previsaoReposicao(
            @RequestParam(defaultValue = "30") int diasParaVencer
    ) {
        return ResponseEntity.ok(relatorioService.previsaoReposicao(diasParaVencer));
    }
}

package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.AcumuladoMensalDTO;
import com.grupo6.gestao_florestal_api.dto.ConfirmacaoPlantioDTO;
import com.grupo6.gestao_florestal_api.dto.MetaPlantioRequestDTO;
import com.grupo6.gestao_florestal_api.dto.MetaPlantioResponseDTO;
import com.grupo6.gestao_florestal_api.service.RelatorioPlantioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios/plantio")
@RequiredArgsConstructor
public class RelatorioPlantioController {

    private final RelatorioPlantioService relatorioService;

    // Confirmação completa do registro com número de protocolo
    // COLABORADOR acessa só o próprio; ADMIN acessa qualquer um
    @GetMapping("/confirmacao/{id}")
    @PreAuthorize("hasAnyRole('ROLE_COLABORADOR', 'ROLE_ADMIN')")
    public ResponseEntity<ConfirmacaoPlantioDTO> confirmacao(
            @PathVariable UUID id,
            Authentication auth
    ) {
        return ResponseEntity.ok(relatorioService.confirmarRegistro(id, auth));
    }

    // Total de mudas no período + percentual da meta
    // COLABORADOR acessa só os próprios registros; ADMIN acessa todos
    @GetMapping("/acumulado-mensal")
    @PreAuthorize("hasAnyRole('ROLE_COLABORADOR', 'ROLE_ADMIN')")
    public ResponseEntity<AcumuladoMensalDTO> acumuladoMensal(
            @RequestParam UUID colaboradorId,
            @RequestParam int mes,
            @RequestParam int ano,
            Authentication auth
    ) {
        return ResponseEntity.ok(relatorioService.acumuladoMensal(colaboradorId, mes, ano, auth));
    }

    // CRUD de metas — somente ADMIN

    @PostMapping("/metas")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MetaPlantioResponseDTO> criarMeta(
            @Valid @RequestBody MetaPlantioRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(relatorioService.criarOuAtualizarMeta(dto));
    }

    @GetMapping("/metas")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<MetaPlantioResponseDTO>> listarMetas() {
        return ResponseEntity.ok(relatorioService.listarMetas());
    }
}

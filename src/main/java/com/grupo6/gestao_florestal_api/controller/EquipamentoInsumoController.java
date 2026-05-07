package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;
import com.grupo6.gestao_florestal_api.dto.AlertaEstoqueResponseDTO;
import com.grupo6.gestao_florestal_api.dto.EquipamentoInsumoRequestDTO;
import com.grupo6.gestao_florestal_api.dto.EquipamentoInsumoResponseDTO;
import com.grupo6.gestao_florestal_api.service.EquipamentoInsumoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipamentos")
@RequiredArgsConstructor
public class EquipamentoInsumoController {

    private final EquipamentoInsumoService equipamentoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipamentoInsumoResponseDTO> criar(
            @Valid @RequestBody EquipamentoInsumoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipamentoService.criar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EquipamentoInsumoResponseDTO>> listar(
            @RequestParam(required = false) CategoriaEquipamento categoria,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "descricao") Pageable pageable
    ) {
        return ResponseEntity.ok(equipamentoService.listar(categoria, localizacao, ativo, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipamentoInsumoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(equipamentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipamentoInsumoResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody EquipamentoInsumoRequestDTO dto
    ) {
        return ResponseEntity.ok(equipamentoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipamentoInsumoResponseDTO> alterarStatus(
            @PathVariable UUID id,
            @RequestParam boolean ativo
    ) {
        return ResponseEntity.ok(equipamentoService.alterarStatus(id, ativo));
    }

    // Retorna todos os itens com quantidade <= estoqueMinimo, com percentualRestante
    @GetMapping("/alertas-estoque")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertaEstoqueResponseDTO>> alertasEstoque() {
        return ResponseEntity.ok(equipamentoService.listarAlertasEstoque());
    }

    // DELETE bloqueado — retorna 405
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNaoPermitido(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}

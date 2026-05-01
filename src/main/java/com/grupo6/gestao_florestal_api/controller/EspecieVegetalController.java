package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import com.grupo6.gestao_florestal_api.dto.EspecieVegetalRequestDTO;
import com.grupo6.gestao_florestal_api.dto.EspecieVegetalResponseDTO;
import com.grupo6.gestao_florestal_api.service.EspecieVegetalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/especies")
@RequiredArgsConstructor
public class EspecieVegetalController {

    private final EspecieVegetalService especieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspecieVegetalResponseDTO> criar(@Valid @RequestBody EspecieVegetalRequestDTO dto) {
        EspecieVegetalResponseDTO response = especieService.criar(dto);
        return ResponseEntity
                .created(URI.create("/api/especies/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspecieVegetalResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(especieService.buscarPorId(id));
    }

    @GetMapping("/alertas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EspecieVegetalResponseDTO>> getEspeciesAmeacadas() {
        return ResponseEntity.ok(especieService.getEspeciesAmeacadas());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EspecieVegetalResponseDTO>> listar(
            @RequestParam(required = false) StatusConservacao statusConservacao,
            @RequestParam(required = false) Porte porte,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 10, sort = "nomeCientifico", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<EspecieVegetalResponseDTO> page = especieService.listar(statusConservacao, porte, ativo, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspecieVegetalResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody EspecieVegetalRequestDTO dto) {

        return ResponseEntity.ok(especieService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EspecieVegetalResponseDTO> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(especieService.inativar(id));
    }
}

package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;
import com.grupo6.gestao_florestal_api.dto.ColaboradorRequestDTO;
import com.grupo6.gestao_florestal_api.dto.ColaboradorResponseDTO;
import com.grupo6.gestao_florestal_api.service.ColaboradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ColaboradorResponseDTO> criar(@Valid @RequestBody ColaboradorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colaboradorService.criar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ColaboradorResponseDTO>> listar(
            @RequestParam(required = false) FuncaoColaborador funcao,
            @RequestParam(required = false) String areaAtuacao,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nomeCompleto") Pageable pageable
    ) {
        return ResponseEntity.ok(colaboradorService.listar(funcao, areaAtuacao, ativo, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ColaboradorResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(colaboradorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ColaboradorResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ColaboradorRequestDTO dto
    ) {
        return ResponseEntity.ok(colaboradorService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ColaboradorResponseDTO> alterarStatus(
            @PathVariable UUID id,
            @RequestParam boolean ativo
    ) {
        return ResponseEntity.ok(colaboradorService.alterarStatus(id, ativo));
    }

    // DELETE explicitamente bloqueado — retorna 405 Method Not Allowed
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNaoPermitido(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}

package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.PlantioRequestDTO;
import com.grupo6.gestao_florestal_api.dto.PlantioResponseDTO;
import com.grupo6.gestao_florestal_api.service.PlantioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/plantios")
@RequiredArgsConstructor
public class PlantioController {

    private final PlantioService plantioService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<PlantioResponseDTO> registrar(@Valid @RequestBody PlantioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantioService.registrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<Page<PlantioResponseDTO>> listar(
            @RequestParam(required = false) UUID areaId,
            @RequestParam(required = false) UUID especieId,
            @RequestParam(required = false) UUID colaboradorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable
    ) {
        return ResponseEntity.ok(plantioService.listar(areaId, especieId, colaboradorId, inicio, fim, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<PlantioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(plantioService.buscarPorId(id));
    }
}

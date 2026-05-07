package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;
import com.grupo6.gestao_florestal_api.dto.AreaFlorestalRequestDTO;
import com.grupo6.gestao_florestal_api.dto.AreaFlorestalResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RelatorioBiomaResponseDTO;
import com.grupo6.gestao_florestal_api.service.AreaFlorestalService;
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
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaFlorestalController {

    private final AreaFlorestalService areaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaFlorestalResponseDTO> criar(@Valid @RequestBody AreaFlorestalRequestDTO dto) {
        AreaFlorestalResponseDTO response = areaService.criar(dto);
        return ResponseEntity.created(URI.create("/api/areas/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COLABORADOR')")
    public ResponseEntity<AreaFlorestalResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COLABORADOR')")
    public ResponseEntity<Page<AreaFlorestalResponseDTO>> listar(
            @RequestParam(required = false) StatusArea status,
            @RequestParam(required = false) Bioma bioma,
            @RequestParam(required = false) TipoFloresta tipoFloresta,
            @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(areaService.listar(status, bioma, tipoFloresta, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaFlorestalResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody AreaFlorestalRequestDTO dto) {
        return ResponseEntity.ok(areaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AreaFlorestalResponseDTO> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(areaService.inativar(id));
    }

    @GetMapping("/relatorio/consolidado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RelatorioBiomaResponseDTO>> getRelatorioConsolidado(
            @RequestParam(required = false) StatusArea status) {
        return ResponseEntity.ok(areaService.getRelatorioConsolidadoPorBioma(status));
    }
}


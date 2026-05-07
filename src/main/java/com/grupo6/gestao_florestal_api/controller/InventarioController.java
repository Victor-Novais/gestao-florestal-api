package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import com.grupo6.gestao_florestal_api.dto.InventarioRequestDTO;
import com.grupo6.gestao_florestal_api.dto.InventarioResponseDTO;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;
    private final ColaboradorRepository colaboradorRepository;


    @PostMapping
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<InventarioResponseDTO> criar(
            @Valid @RequestBody InventarioRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID colaboradorId = resolveColaboradorId(dto, userDetails);

        InventarioResponseDTO response = inventarioService.criar(dto, colaboradorId);

        return ResponseEntity
                .created(URI.create("/api/inventarios/" + response.getId()))
                .body(response);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<Page<InventarioResponseDTO>> listar(
            @RequestParam(required = false) UUID area,
            @RequestParam(required = false) EstadoGeral estado,
            @RequestParam(required = false) UUID colaborador,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<InventarioResponseDTO> page = inventarioService.listar(
                area, estado, colaborador, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<InventarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(inventarioService.buscarPorId(id));
    }


    @GetMapping("/parcela")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<List<InventarioResponseDTO>> buscarPorParcela(
            @RequestParam UUID area,
            @RequestParam String parcela) {

        return ResponseEntity.ok(inventarioService.buscarPorParcela(area, parcela));
    }


    private UUID buscarColaboradorIdPorUsername(String username) {
        return colaboradorRepository.findByUserUsername(username)
                .map(Colaborador::getId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Colaborador não encontrado para o usuário: " + username)); // ← corrigido
    }

    private UUID resolveColaboradorId(InventarioRequestDTO dto, UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        // Colaborador: sempre usa o colaborador vinculado ao usuário
        if (!isAdmin) {
            return buscarColaboradorIdPorUsername(userDetails.getUsername());
        }

        // Admin: pode informar colaboradorId no payload; caso não informe, tenta o vínculo do próprio admin
        if (dto.getColaboradorId() != null) {
            return dto.getColaboradorId();
        }

        return buscarColaboradorIdPorUsername(userDetails.getUsername());
    }
}
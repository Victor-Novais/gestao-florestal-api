package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaRequestDTO;
import com.grupo6.gestao_florestal_api.dto.OcorrenciaResponseDTO;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.service.OcorrenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;
    private final ColaboradorRepository colaboradorRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<OcorrenciaResponseDTO> criar(
            @Valid @RequestBody OcorrenciaRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID colaboradorId = buscarColaboradorIdPorUsername(userDetails.getUsername());
        OcorrenciaResponseDTO response = ocorrenciaService.criar(dto, colaboradorId);

        return ResponseEntity
                .created(URI.create("/api/ocorrencias/" + response.getId()))
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<Page<OcorrenciaResponseDTO>> listar(
            @RequestParam(required = false) TipoOcorrencia tipo,
            @RequestParam(required = false) UrgenciaOcorrencia urgencia,
            @RequestParam(required = false) UUID area,
            @RequestParam(required = false) UUID colaborador,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @PageableDefault(size = 10, sort = "dataRegistro", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OcorrenciaResponseDTO> page = ocorrenciaService.listar(
                tipo, urgencia, area, colaborador, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/protocolo/{numeroProtocolo}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorProtocolo(@PathVariable String numeroProtocolo) {
        return ResponseEntity.ok(ocorrenciaService.buscarPorProtocolo(numeroProtocolo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ADMIN')")
    public ResponseEntity<OcorrenciaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ocorrenciaService.buscarPorId(id));
    }

    private UUID buscarColaboradorIdPorUsername(String username) {
        return colaboradorRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"))
                .getId();
    }
}
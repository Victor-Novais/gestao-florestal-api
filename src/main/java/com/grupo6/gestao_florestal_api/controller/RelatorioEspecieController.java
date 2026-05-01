package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import com.grupo6.gestao_florestal_api.dto.FichaTecnicaEspecieDTO;
import com.grupo6.gestao_florestal_api.dto.AlertaEspecieAmeacadaDTO;
import com.grupo6.gestao_florestal_api.service.RelatorioEspecieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/especies")
@RequiredArgsConstructor
public class RelatorioEspecieController {

    private final RelatorioEspecieService relatorioEspecieService;

    @GetMapping("/fichas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FichaTecnicaEspecieDTO>> getFichasTecnicas(
            @RequestParam(required = false) StatusConservacao statusConservacao,
            @PageableDefault(size = 10, sort = "nomeCientifico", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<FichaTecnicaEspecieDTO> page = relatorioEspecieService.getFichasTecnicas(statusConservacao, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/alertas-ameacadas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertaEspecieAmeacadaDTO>> getAlertasEspeciesAmeacadas() {
        List<AlertaEspecieAmeacadaDTO> alertas = relatorioEspecieService.getAlertasEspeciesAmeacadas();
        return ResponseEntity.ok(alertas);
    }
}

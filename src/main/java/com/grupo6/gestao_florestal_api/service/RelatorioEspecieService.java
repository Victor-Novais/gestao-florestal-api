package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.EspecieVegetal;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import com.grupo6.gestao_florestal_api.dto.FichaTecnicaEspecieDTO;
import com.grupo6.gestao_florestal_api.dto.AlertaEspecieAmeacadaDTO;
import com.grupo6.gestao_florestal_api.repository.EspecieVegetalRepository;
import com.grupo6.gestao_florestal_api.repository.InventarioEspecieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioEspecieService {

    private final EspecieVegetalRepository especieRepository;
    private final InventarioEspecieRepository inventarioEspecieRepository;

    public Page<FichaTecnicaEspecieDTO> getFichasTecnicas(StatusConservacao statusConservacao, Pageable pageable) {
        Page<EspecieVegetal> page;

        if (statusConservacao != null) {
            page = especieRepository.findByAtivoTrueAndStatusConservacao(statusConservacao, pageable);
        } else {
            page = especieRepository.findByAtivoTrue(pageable);
        }

        return page.map(this::toFichaTecnicaDTO);
    }

    public List<AlertaEspecieAmeacadaDTO> getAlertasEspeciesAmeacadas() {
        List<EspecieVegetal> especies = especieRepository
                .findByAtivoTrueAndStatusConservacao(StatusConservacao.AMEACADA);

        return especies.stream()
                .map(this::toAlertaDTO)
                .collect(Collectors.toList());
    }

    private FichaTecnicaEspecieDTO toFichaTecnicaDTO(EspecieVegetal especie) {
        Long totalAreas = inventarioEspecieRepository.countAreasPorEspecie(especie.getId());

        return new FichaTecnicaEspecieDTO(
                especie.getId(),
                especie.getNomeCientifico(),
                especie.getNomePopular(),
                especie.getFamiliaBotanica(),
                especie.getPorte(),
                especie.getStatusConservacao(),
                especie.getCicloVidaAnos(),
                especie.getExigenciasClimaticas(),
                especie.getExigenciasSolo(),
                especie.getNativa(),
                especie.getCriadoEm() != null ? especie.getCriadoEm() : LocalDateTime.now(),
                totalAreas != null ? totalAreas : 0L
        );
    }

    private AlertaEspecieAmeacadaDTO toAlertaDTO(EspecieVegetal especie) {
        Long totalAreas = inventarioEspecieRepository.countAreasPorEspecie(especie.getId());

        return new AlertaEspecieAmeacadaDTO(
                especie.getId(),
                especie.getNomeCientifico(),
                especie.getNomePopular(),
                especie.getPorte(),
                especie.getStatusConservacao(),
                especie.getCriadoEm() != null ? especie.getCriadoEm() : LocalDateTime.now(),
                totalAreas != null ? totalAreas : 0L
        );
    }
}
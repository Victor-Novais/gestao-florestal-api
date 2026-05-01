package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.EspecieVegetal;
import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import com.grupo6.gestao_florestal_api.dto.EspecieVegetalRequestDTO;
import com.grupo6.gestao_florestal_api.dto.EspecieVegetalResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.EspecieVegetalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecieVegetalService {

    private final EspecieVegetalRepository especieRepository;

    @Transactional
    public EspecieVegetalResponseDTO criar(EspecieVegetalRequestDTO dto) {
        if (especieRepository.existsByNomeCientifico(dto.getNomeCientifico())) {
            throw new BusinessException("Nome científico já cadastrado: " + dto.getNomeCientifico());
        }

        EspecieVegetal especie = EspecieVegetal.builder()
                .nomeCientifico(dto.getNomeCientifico())
                .nomePopular(dto.getNomePopular())
                .familiaBotanica(dto.getFamiliaBotanica())
                .porte(dto.getPorte())
                .statusConservacao(dto.getStatusConservacao())
                .cicloVidaAnos(dto.getCicloVidaAnos())
                .exigenciasClimaticas(dto.getExigenciasClimaticas())
                .exigenciasSolo(dto.getExigenciasSolo())
                .nativa(dto.getNativa() != null ? dto.getNativa() : true)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .build();

        return toResponseDTO(especieRepository.save(especie));
    }

    public EspecieVegetalResponseDTO buscarPorId(UUID id) {
        EspecieVegetal especie = especieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espécie não encontrada com ID: " + id));
        return toResponseDTO(especie);
    }

    public Page<EspecieVegetalResponseDTO> listar(StatusConservacao statusConservacao, Porte porte, Boolean ativo, Pageable pageable) {
        Page<EspecieVegetal> page;

        if (statusConservacao != null) {
            page = especieRepository.findByStatusConservacao(statusConservacao, pageable);
        } else if (porte != null) {
            page = especieRepository.findByPorte(porte, pageable);
        } else if (ativo != null && ativo) {
            page = especieRepository.findByAtivoTrue(pageable);
        } else {
            page = especieRepository.findAll(pageable);
        }

        return page.map(this::toResponseDTO);
    }

    public List<EspecieVegetalResponseDTO> getEspeciesAmeacadas() {
        return especieRepository.findByAtivoTrueAndStatusConservacao(StatusConservacao.AMEACADA)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EspecieVegetalResponseDTO atualizar(UUID id, EspecieVegetalRequestDTO dto) {
        EspecieVegetal especie = especieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espécie não encontrada com ID: " + id));

        if (!especie.getNomeCientifico().equals(dto.getNomeCientifico()) &&
                especieRepository.existsByNomeCientifico(dto.getNomeCientifico())) {
            throw new BusinessException("Nome científico já cadastrado: " + dto.getNomeCientifico());
        }

        especie.setNomeCientifico(dto.getNomeCientifico());
        especie.setNomePopular(dto.getNomePopular());
        especie.setFamiliaBotanica(dto.getFamiliaBotanica());
        especie.setPorte(dto.getPorte());
        especie.setStatusConservacao(dto.getStatusConservacao());
        especie.setCicloVidaAnos(dto.getCicloVidaAnos());
        especie.setExigenciasClimaticas(dto.getExigenciasClimaticas());
        especie.setExigenciasSolo(dto.getExigenciasSolo());
        if (dto.getNativa() != null) {
            especie.setNativa(dto.getNativa());
        }

        return toResponseDTO(especieRepository.save(especie));
    }

    @Transactional
    public EspecieVegetalResponseDTO inativar(UUID id) {
        EspecieVegetal especie = especieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Espécie não encontrada com ID: " + id));
        especie.setAtivo(false);
        return toResponseDTO(especieRepository.save(especie));
    }

    private EspecieVegetalResponseDTO toResponseDTO(EspecieVegetal especie) {
        return new EspecieVegetalResponseDTO(
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
                especie.getAtivo(),
                especie.getCriadoEm()
        );
    }
}
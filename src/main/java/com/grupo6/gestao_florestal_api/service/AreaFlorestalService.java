package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.AreaFlorestal;
import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;
import com.grupo6.gestao_florestal_api.dto.AreaFlorestalRequestDTO;
import com.grupo6.gestao_florestal_api.dto.AreaFlorestalResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RelatorioBiomaResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RelatorioBiomaResponseDTO.RelatorioPorTipoDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.AreaFlorestalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AreaFlorestalService {

    private final AreaFlorestalRepository areaRepository;

    @Transactional
    public AreaFlorestalResponseDTO criar(AreaFlorestalRequestDTO dto) {
        if (areaRepository.existsByIdentificadorUnico(dto.getIdentificadorUnico())) {
            throw new BusinessException("Identificador único já cadastrado: " + dto.getIdentificadorUnico());
        }

        AreaFlorestal area = AreaFlorestal.builder()
                .identificadorUnico(dto.getIdentificadorUnico())
                .nome(dto.getNome())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .municipio(dto.getMunicipio())
                .estado(dto.getEstado())
                .hectares(dto.getHectares())
                .tipoFloresta(dto.getTipoFloresta())
                .bioma(dto.getBioma())
                .status(dto.getStatus() != null ? dto.getStatus() : StatusArea.ATIVA)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .build();

        return toResponseDTO(areaRepository.save(area));
    }

    public AreaFlorestalResponseDTO buscarPorId(UUID id) {
        AreaFlorestal area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área Florestal não encontrada com ID: " + id));
        return toResponseDTO(area);
    }

    public Page<AreaFlorestalResponseDTO> listar(
            StatusArea status,
            Bioma bioma,
            TipoFloresta tipoFloresta,
            Pageable pageable) {

        Page<AreaFlorestal> page;

        if (status != null) {
            page = areaRepository.findByStatus(status, pageable);
        } else if (bioma != null) {
            page = areaRepository.findByBioma(bioma, pageable);
        } else if (tipoFloresta != null) {
            page = areaRepository.findByTipoFloresta(tipoFloresta, pageable);
        } else {
            page = areaRepository.findByAtivoTrue(pageable);
        }

        return page.map(this::toResponseDTO);
    }

    @Transactional
    public AreaFlorestalResponseDTO atualizar(UUID id, AreaFlorestalRequestDTO dto) {
        AreaFlorestal area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área Florestal não encontrada com ID: " + id));

        if (!area.getIdentificadorUnico().equals(dto.getIdentificadorUnico()) &&
                areaRepository.existsByIdentificadorUnico(dto.getIdentificadorUnico())) {
            throw new BusinessException("Identificador único já cadastrado: " + dto.getIdentificadorUnico());
        }

        area.setIdentificadorUnico(dto.getIdentificadorUnico());
        area.setNome(dto.getNome());
        area.setLatitude(dto.getLatitude());
        area.setLongitude(dto.getLongitude());
        area.setMunicipio(dto.getMunicipio());
        area.setEstado(dto.getEstado());
        area.setHectares(dto.getHectares());
        area.setTipoFloresta(dto.getTipoFloresta());
        area.setBioma(dto.getBioma());
        if (dto.getStatus() != null) {
            area.setStatus(dto.getStatus());
        }

        return toResponseDTO(areaRepository.save(area));
    }

    @Transactional
    public AreaFlorestalResponseDTO inativar(UUID id) {
        AreaFlorestal area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área Florestal não encontrada com ID: " + id));

        area.setAtivo(false);
        area.setDataInativacao(LocalDateTime.now());

        return toResponseDTO(areaRepository.save(area));
    }

    public List<RelatorioBiomaResponseDTO> getRelatorioConsolidadoPorBioma(StatusArea status) {
        String statusParam = status != null ? status.name() : null;
        List<Object[]> resultados = areaRepository.getRelatorioConsolidadoPorBioma(statusParam);

        Map<Bioma, Map<TipoFloresta, RelatorioPorTipoDTO>> mapa = new LinkedHashMap<>();

        for (Object[] row : resultados) {
            Bioma bioma = Bioma.valueOf((String) row[0]);
            TipoFloresta tipo = TipoFloresta.valueOf((String) row[1]);
            Long totalAreas = ((Number) row[2]).longValue();
            BigDecimal totalHectares = (BigDecimal) row[3];

            mapa.computeIfAbsent(bioma, k -> new LinkedHashMap<>())
                    .put(tipo, new RelatorioPorTipoDTO(tipo, totalAreas, totalHectares));
        }

        List<RelatorioBiomaResponseDTO> response = new ArrayList<>();
        for (Map.Entry<Bioma, Map<TipoFloresta, RelatorioPorTipoDTO>> entry : mapa.entrySet()) {
            Bioma bioma = entry.getKey();
            Map<TipoFloresta, RelatorioPorTipoDTO> tiposMap = entry.getValue();

            Long totalAreas = tiposMap.values().stream()
                    .mapToLong(RelatorioPorTipoDTO::totalAreas)
                    .sum();
            BigDecimal totalHectares = tiposMap.values().stream()
                    .map(RelatorioPorTipoDTO::totalHectares)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.add(new RelatorioBiomaResponseDTO(
                    bioma, totalAreas, totalHectares, new ArrayList<>(tiposMap.values())
            ));
        }
        return response;
    }

    private AreaFlorestalResponseDTO toResponseDTO(AreaFlorestal area) {
        return new AreaFlorestalResponseDTO(
                area.getId(),
                area.getIdentificadorUnico(),
                area.getNome(),
                area.getLatitude(),
                area.getLongitude(),
                area.getMunicipio(),
                area.getEstado(),
                area.getHectares(),
                area.getTipoFloresta(),
                area.getBioma(),
                area.getStatus(),
                area.isAtivo(),
                area.getCriadoEm(),
                area.getDataInativacao()
        );
    }
}

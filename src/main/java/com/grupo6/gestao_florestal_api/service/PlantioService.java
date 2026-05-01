package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.AreaFlorestal;
import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.EspecieVegetal;
import com.grupo6.gestao_florestal_api.domain.RegistroPlantio;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.dto.PlantioRequestDTO;
import com.grupo6.gestao_florestal_api.dto.PlantioResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.AreaFlorestalRepository;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.repository.EspecieVegetalRepository;
import com.grupo6.gestao_florestal_api.repository.RegistroPlantioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class PlantioService {

    private final RegistroPlantioRepository plantioRepository;
    private final AreaFlorestalRepository areaRepository;
    private final EspecieVegetalRepository especieRepository;
    private final ColaboradorRepository colaboradorRepository;

    private final AtomicLong sequencial = new AtomicLong(System.currentTimeMillis() % 100000);

    @Transactional
    public PlantioResponseDTO registrar(PlantioRequestDTO dto) {
        AreaFlorestal area = areaRepository.findById(dto.areaFlorestalId())
                .orElseThrow(() -> new EntityNotFoundException("Área florestal não encontrada: " + dto.areaFlorestalId()));

        if (area.getStatus() != StatusArea.ATIVA) {
            throw new BusinessException("Plantio só pode ser registrado em áreas com status ATIVA. Status atual: " + area.getStatus());
        }

        EspecieVegetal especie = especieRepository.findById(dto.especieVegetalId())
                .orElseThrow(() -> new EntityNotFoundException("Espécie vegetal não encontrada: " + dto.especieVegetalId()));

        if (!especie.getAtivo()) {
            throw new BusinessException("Espécie vegetal está inativa: " + especie.getNomePopular());
        }

        Colaborador colaborador = colaboradorRepository.findById(dto.colaboradorId())
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado: " + dto.colaboradorId()));

        RegistroPlantio plantio = RegistroPlantio.builder()
                .dataHora(dto.dataHora())
                .areaFlorestal(area)
                .especieVegetal(especie)
                .colaborador(colaborador)
                .quantidadeMudas(dto.quantidadeMudas())
                .latitudeTalhao(dto.latitudeTalhao())
                .longitudeTalhao(dto.longitudeTalhao())
                .temperatura(dto.temperatura())
                .umidade(dto.umidade())
                .chuva(dto.chuva())
                .metodoPlantio(dto.metodoPlantio())
                .observacoes(dto.observacoes())
                .numProtocolo(gerarProtocolo())
                .build();

        return PlantioResponseDTO.from(plantioRepository.save(plantio));
    }

    @Transactional(readOnly = true)
    public Page<PlantioResponseDTO> listar(
            UUID areaId, UUID especieId, UUID colaboradorId,
            LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return plantioRepository
                .findWithFilters(areaId, especieId, colaboradorId, inicio, fim, pageable)
                .map(PlantioResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public PlantioResponseDTO buscarPorId(UUID id) {
        return PlantioResponseDTO.from(
                plantioRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Registro de plantio não encontrado: " + id))
        );
    }

    private String gerarProtocolo() {
        String ano = String.valueOf(LocalDateTime.now().getYear());
        String seq = String.format("%05d", sequencial.incrementAndGet());
        return "PLT-" + ano + "-" + seq;
    }
}

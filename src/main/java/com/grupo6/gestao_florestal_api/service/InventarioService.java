package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.AreaFlorestal;
import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.EspecieVegetal;
import com.grupo6.gestao_florestal_api.domain.InventarioEspecie;
import com.grupo6.gestao_florestal_api.domain.InventarioFlorestal;
import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.dto.InventarioEspecieItemDTO;
import com.grupo6.gestao_florestal_api.dto.InventarioEspecieResponseDTO;
import com.grupo6.gestao_florestal_api.dto.InventarioRequestDTO;
import com.grupo6.gestao_florestal_api.dto.InventarioResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.AreaFlorestalRepository;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.repository.EspecieVegetalRepository;
import com.grupo6.gestao_florestal_api.repository.InventarioFlorestalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioFlorestalRepository inventarioRepository;
    private final AreaFlorestalRepository areaRepository;
    private final EspecieVegetalRepository especieRepository;
    private final ColaboradorRepository colaboradorRepository;


    @Transactional
    public InventarioResponseDTO criar(InventarioRequestDTO dto, UUID colaboradorId) {


        AreaFlorestal area = areaRepository.findById(dto.getAreaFlorestalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Área Florestal não encontrada com ID: " + dto.getAreaFlorestalId()));

        if (area.getStatus() != StatusArea.ATIVA) {
            throw new BusinessException("Apenas áreas ativas podem receber inventário");
        }


        Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Colaborador não encontrado com ID: " + colaboradorId));


        InventarioFlorestal inventario = InventarioFlorestal.builder()
                .numeroParcela(dto.getNumeroParcela())
                .areaFlorestal(area)
                .dataVistoria(dto.getDataVistoria())
                .presencaPragas(dto.getPresencaPragas())
                .descricaoPragas(dto.getDescricaoPragas())
                .estadoGeral(dto.getEstadoGeral())
                .colaborador(colaborador)
                .build();


        for (InventarioEspecieItemDTO itemDTO : dto.getEspecies()) {
            EspecieVegetal especie = especieRepository.findById(itemDTO.getEspecieId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Espécie Vegetal não encontrada com ID: " + itemDTO.getEspecieId()));

            InventarioEspecie inventarioEspecie = InventarioEspecie.builder()
                    .inventario(inventario)
                    .especieVegetal(especie)
                    .quantidade(itemDTO.getQuantidade())
                    .dapMedio(itemDTO.getDapMedio())
                    .alturaMedia(itemDTO.getAlturaMedia())
                    .build();

            inventario.getEspecies().add(inventarioEspecie);
        }


        InventarioFlorestal salvo = inventarioRepository.save(inventario);
        return toResponseDTO(salvo);
    }


    public InventarioResponseDTO buscarPorId(UUID id) {
        InventarioFlorestal inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inventário Florestal não encontrado com ID: " + id));
        return toResponseDTO(inventario);
    }


    public Page<InventarioResponseDTO> listar(
            UUID areaId,
            EstadoGeral estado,
            UUID colaboradorId,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable) {

        Page<InventarioFlorestal> page;


        if (areaId != null) {
            page = inventarioRepository.findByAreaFlorestalId(areaId, pageable);
        } else if (estado != null) {
            page = inventarioRepository.findByEstadoGeral(estado, pageable);
        } else if (colaboradorId != null) {
            page = inventarioRepository.findByColaboradorId(colaboradorId, pageable);
        } else if (dataInicio != null || dataFim != null) {

            LocalDate inicio = dataInicio != null ? dataInicio : LocalDate.now().minusYears(10);
            LocalDate fim = dataFim != null ? dataFim : LocalDate.now();
            page = inventarioRepository.findByDataVistoriaBetween(inicio, fim, pageable);
        } else {
            page = inventarioRepository.findAll(pageable);
        }
        return page.map(this::toResponseDTO);
    }
    public List<InventarioResponseDTO> buscarPorParcela(UUID areaId, String numeroParcela) {
        List<InventarioFlorestal> inventarios = inventarioRepository
                .buscarHistoricoParcela(areaId, numeroParcela, null, null);

        if (inventarios.isEmpty()) {
            throw new EntityNotFoundException(
                    "Nenhum inventário encontrado para parcela " + numeroParcela + " na área " + areaId);
        }

        return inventarios.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }



    private InventarioResponseDTO toResponseDTO(InventarioFlorestal inventario) {
        List<InventarioEspecieResponseDTO> especiesDTO = inventario.getEspecies().stream()
                .map(this::toEspecieResponseDTO)
                .collect(Collectors.toList());

        return InventarioResponseDTO.builder()
                .id(inventario.getId())
                .numeroParcela(inventario.getNumeroParcela())
                .areaFlorestalId(inventario.getAreaFlorestal().getId())
                .areaFlorestalNome(inventario.getAreaFlorestal().getNome())
                .dataVistoria(inventario.getDataVistoria())
                .presencaPragas(inventario.getPresencaPragas())
                .descricaoPragas(inventario.getDescricaoPragas())
                .estadoGeral(inventario.getEstadoGeral())
                .colaboradorId(inventario.getColaborador().getId())
                .colaboradorNome(inventario.getColaborador().getNomeCompleto())
                .criadoEm(inventario.getCriadoEm())
                .especies(especiesDTO)
                .build();
    }

    private InventarioEspecieResponseDTO toEspecieResponseDTO(InventarioEspecie ie) {
        return InventarioEspecieResponseDTO.builder()
                .id(ie.getId())
                .especieId(ie.getEspecieVegetal().getId())
                .especieNome(ie.getEspecieVegetal().getNomePopular())
                .especieNome(ie.getEspecieVegetal().getNomeCientifico())
                .quantidade(ie.getQuantidade())
                .dapMedio(ie.getDapMedio())
                .alturaMedia(ie.getAlturaMedia())
                .build();
    }
}

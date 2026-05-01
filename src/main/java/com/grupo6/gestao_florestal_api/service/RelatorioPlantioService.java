package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.AreaFlorestal;
import com.grupo6.gestao_florestal_api.domain.MetaPlantio;
import com.grupo6.gestao_florestal_api.domain.RegistroPlantio;
import com.grupo6.gestao_florestal_api.dto.AcumuladoMensalDTO;
import com.grupo6.gestao_florestal_api.dto.ConfirmacaoPlantioDTO;
import com.grupo6.gestao_florestal_api.dto.MetaPlantioRequestDTO;
import com.grupo6.gestao_florestal_api.dto.MetaPlantioResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.AreaFlorestalRepository;
import com.grupo6.gestao_florestal_api.repository.MetaPlantioRepository;
import com.grupo6.gestao_florestal_api.repository.RegistroPlantioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioPlantioService {

    private final RegistroPlantioRepository plantioRepository;
    private final MetaPlantioRepository metaRepository;
    private final AreaFlorestalRepository areaRepository;

    @Transactional(readOnly = true)
    public ConfirmacaoPlantioDTO confirmarRegistro(UUID plantioId, Authentication auth) {
        RegistroPlantio plantio = plantioRepository.findById(plantioId)
                .orElseThrow(() -> new EntityNotFoundException("Plantio não encontrado: " + plantioId));

        if (!isAdmin(auth)) {
            String usernameLogado = auth.getName();
            String usernameColaborador = plantio.getColaborador().getUser() != null
                    ? plantio.getColaborador().getUser().getUsername()
                    : null;
            if (!usernameLogado.equals(usernameColaborador)) {
                throw new AccessDeniedException("Acesso negado ao registro de outro colaborador");
            }
        }

        return ConfirmacaoPlantioDTO.from(plantio);
    }

    @Transactional(readOnly = true)
    public AcumuladoMensalDTO acumuladoMensal(UUID colaboradorId, int mes, int ano, Authentication auth) {
        int total = plantioRepository.sumMudasByColaboradorAndPeriodo(colaboradorId, ano, mes);

        List<MetaPlantio> metas = metaRepository.findByMesAndAno(mes, ano);
        Integer metaTotal = metas.isEmpty()
                ? null
                : metas.stream().mapToInt(MetaPlantio::getQuantidadeMeta).sum();

        return AcumuladoMensalDTO.of(mes, ano, total, metaTotal);
    }

    @Transactional
    public MetaPlantioResponseDTO criarOuAtualizarMeta(MetaPlantioRequestDTO dto) {
        AreaFlorestal area = areaRepository.findById(dto.areaFlorestalId())
                .orElseThrow(() -> new EntityNotFoundException("Área florestal não encontrada: " + dto.areaFlorestalId()));

        MetaPlantio meta = metaRepository
                .findByAreaFlorestal_IdAndMesAndAno(dto.areaFlorestalId(), dto.mes(), dto.ano())
                .orElseGet(MetaPlantio::new);

        meta.setAreaFlorestal(area);
        meta.setMes(dto.mes());
        meta.setAno(dto.ano());
        meta.setQuantidadeMeta(dto.quantidadeMeta());

        return MetaPlantioResponseDTO.from(metaRepository.save(meta));
    }

    @Transactional(readOnly = true)
    public List<MetaPlantioResponseDTO> listarMetas() {
        return metaRepository.findAll()
                .stream()
                .map(MetaPlantioResponseDTO::from)
                .toList();
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}

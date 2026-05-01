package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.EquipamentoInsumo;
import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;
import com.grupo6.gestao_florestal_api.dto.AlertaEstoqueResponseDTO;
import com.grupo6.gestao_florestal_api.dto.EquipamentoInsumoRequestDTO;
import com.grupo6.gestao_florestal_api.dto.EquipamentoInsumoResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.repository.EquipamentoInsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipamentoInsumoService {

    private final EquipamentoInsumoRepository equipamentoRepository;
    private final ColaboradorRepository colaboradorRepository;

    @Transactional
    public EquipamentoInsumoResponseDTO criar(EquipamentoInsumoRequestDTO dto) {
        if (equipamentoRepository.existsByCodigoPatrimonial(dto.codigoPatrimonial())) {
            throw new BusinessException("Código patrimonial já cadastrado: " + dto.codigoPatrimonial());
        }

        EquipamentoInsumo equipamento = EquipamentoInsumo.builder()
                .codigoPatrimonial(dto.codigoPatrimonial())
                .descricao(dto.descricao())
                .categoria(dto.categoria())
                .quantidade(dto.quantidade())
                .unidadeMedida(dto.unidadeMedida())
                .localizacaoAtual(dto.localizacaoAtual())
                .dataAquisicao(dto.dataAquisicao())
                .vidaUtilEstimada(dto.vidaUtilEstimada())
                .estoqueMinimo(dto.estoqueMinimo())
                .ativo(true)
                .responsavel(resolverResponsavel(dto.responsavelId()))
                .build();

        return EquipamentoInsumoResponseDTO.from(equipamentoRepository.save(equipamento));
    }

    @Transactional(readOnly = true)
    public Page<EquipamentoInsumoResponseDTO> listar(
            CategoriaEquipamento categoria, String localizacao, Boolean ativo, Pageable pageable) {
        return equipamentoRepository
                .findWithFilters(categoria, localizacao, ativo, pageable)
                .map(EquipamentoInsumoResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public EquipamentoInsumoResponseDTO buscarPorId(UUID id) {
        return EquipamentoInsumoResponseDTO.from(buscarEntidade(id));
    }

    @Transactional
    public EquipamentoInsumoResponseDTO atualizar(UUID id, EquipamentoInsumoRequestDTO dto) {
        EquipamentoInsumo equipamento = buscarEntidade(id);

        if (!equipamento.getCodigoPatrimonial().equals(dto.codigoPatrimonial())
                && equipamentoRepository.existsByCodigoPatrimonial(dto.codigoPatrimonial())) {
            throw new BusinessException("Código patrimonial já cadastrado: " + dto.codigoPatrimonial());
        }

        equipamento.setCodigoPatrimonial(dto.codigoPatrimonial());
        equipamento.setDescricao(dto.descricao());
        equipamento.setCategoria(dto.categoria());
        equipamento.setQuantidade(dto.quantidade());
        equipamento.setUnidadeMedida(dto.unidadeMedida());
        equipamento.setLocalizacaoAtual(dto.localizacaoAtual());
        equipamento.setDataAquisicao(dto.dataAquisicao());
        equipamento.setVidaUtilEstimada(dto.vidaUtilEstimada());
        equipamento.setEstoqueMinimo(dto.estoqueMinimo());
        equipamento.setResponsavel(resolverResponsavel(dto.responsavelId()));

        return EquipamentoInsumoResponseDTO.from(equipamentoRepository.save(equipamento));
    }

    @Transactional
    public EquipamentoInsumoResponseDTO alterarStatus(UUID id, boolean ativo) {
        EquipamentoInsumo equipamento = buscarEntidade(id);
        equipamento.setAtivo(ativo);
        return EquipamentoInsumoResponseDTO.from(equipamentoRepository.save(equipamento));
    }

    @Transactional(readOnly = true)
    public List<AlertaEstoqueResponseDTO> listarAlertasEstoque() {
        return equipamentoRepository.findAbaixoDoEstoqueMinimo()
                .stream()
                .map(AlertaEstoqueResponseDTO::from)
                .toList();
    }

    private EquipamentoInsumo buscarEntidade(UUID id) {
        return equipamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipamento/insumo não encontrado: " + id));
    }

    private Colaborador resolverResponsavel(UUID responsavelId) {
        if (responsavelId == null) return null;
        return colaboradorRepository.findById(responsavelId)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador responsável não encontrado: " + responsavelId));
    }
}

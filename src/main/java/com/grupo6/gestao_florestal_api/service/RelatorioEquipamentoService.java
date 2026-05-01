package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.dto.InventarioEquipamentoDTO;
import com.grupo6.gestao_florestal_api.dto.PrevisaoReposicaoDTO;
import com.grupo6.gestao_florestal_api.repository.EquipamentoInsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioEquipamentoService {

    private final EquipamentoInsumoRepository equipamentoRepository;

   
    @Transactional(readOnly = true)
    public List<InventarioEquipamentoDTO> inventario() {
        return equipamentoRepository.findAllAtivosOrdenados()
                .stream()
                .map(InventarioEquipamentoDTO::from)
                .toList();
    }

    
    @Transactional(readOnly = true)
    public List<PrevisaoReposicaoDTO> previsaoReposicao(int diasParaVencer) {
        LocalDate dataLimite = LocalDate.now().plusDays(diasParaVencer);

        return equipamentoRepository.findAllAtivosOrdenados()
                .stream()
                .map(PrevisaoReposicaoDTO::from)
                .filter(dto -> dto.diasRestantes() <= diasParaVencer)
                .sorted(Comparator.comparingLong(PrevisaoReposicaoDTO::diasRestantes))
                .toList();
    }
}

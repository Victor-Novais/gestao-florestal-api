package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.InventarioEspecie;
import com.grupo6.gestao_florestal_api.domain.InventarioFlorestal;
import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import com.grupo6.gestao_florestal_api.dto.HistoricoParcelaItemDTO;
import com.grupo6.gestao_florestal_api.dto.VariacaoEspecieDTO;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.repository.InventarioFlorestalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RelatorioInventarioService {

    private final InventarioFlorestalRepository inventarioRepository;
    private final ColaboradorRepository colaboradorRepository;

    @Transactional(readOnly = true)
    public List<HistoricoParcelaItemDTO> buscarHistoricoParcela(
            UUID areaId,
            String parcela,
            LocalDate dataInicio,
            LocalDate dataFim,
            Authentication auth
    ) {
        validarAcessoArea(areaId, auth);

        List<InventarioFlorestal> vistorias = inventarioRepository
                .buscarHistoricoParcela(areaId, parcela, dataInicio, dataFim);

        if (vistorias.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma vistoria encontrada para a parcela " + parcela);
        }

        List<HistoricoParcelaItemDTO> resultado = new ArrayList<>();
        InventarioFlorestal anterior = null;

        for (InventarioFlorestal atual : vistorias) {
            resultado.add(montarItem(atual, anterior));
            anterior = atual;
        }

        return resultado;
    }

    private HistoricoParcelaItemDTO montarItem(InventarioFlorestal atual, InventarioFlorestal anterior) {
        BigDecimal dapMedioAtual = calcularDapMedio(atual);
        BigDecimal variacaoDap = anterior == null ? null : dapMedioAtual.subtract(calcularDapMedio(anterior));
        String mudancaEstadoGeral = anterior == null ? null : anterior.getEstadoGeral() + " -> " + atual.getEstadoGeral();
        String tendencia = anterior == null ? null : calcularTendencia(anterior.getEstadoGeral(), atual.getEstadoGeral());

        return new HistoricoParcelaItemDTO(
                atual.getId(),
                atual.getDataVistoria(),
                atual.getNumeroParcela(),
                atual.getAreaFlorestal().getId(),
                atual.getEstadoGeral(),
                mudancaEstadoGeral,
                dapMedioAtual,
                variacaoDap,
                tendencia,
                montarVariacoesEspecie(atual, anterior)
        );
    }

    private List<VariacaoEspecieDTO> montarVariacoesEspecie(InventarioFlorestal atual, InventarioFlorestal anterior) {
        List<VariacaoEspecieDTO> especies = new ArrayList<>();

        if (anterior == null) {
            List<InventarioEspecie> especiesAtuais = atual.getEspecies()
                    .stream()
                    .sorted(Comparator.comparing(item -> item.getEspecieVegetal().getNomePopular()))
                    .toList();

            for (InventarioEspecie especieAtual : especiesAtuais) {
                especies.add(new VariacaoEspecieDTO(
                        especieAtual.getEspecieVegetal().getId(),
                        especieAtual.getEspecieVegetal().getNomePopular(),
                        especieAtual.getQuantidade(),
                        null,
                        null
                ));
            }

            return especies;
        }

        Map<UUID, InventarioEspecie> especiesAtuais = atual.getEspecies().stream()
                .collect(LinkedHashMap::new,
                        (map, item) -> map.put(item.getEspecieVegetal().getId(), item),
                        LinkedHashMap::putAll);

        Map<UUID, InventarioEspecie> especiesAnteriores = anterior.getEspecies().stream()
                .collect(LinkedHashMap::new,
                        (map, item) -> map.put(item.getEspecieVegetal().getId(), item),
                        LinkedHashMap::putAll);

        List<UUID> especieIds = new ArrayList<>(especiesAtuais.keySet());
        for (UUID especieId : especiesAnteriores.keySet()) {
            if (!especiesAtuais.containsKey(especieId)) {
                especieIds.add(especieId);
            }
        }

        especieIds.stream()
                .sorted(Comparator.comparing(especieId -> {
                    InventarioEspecie especieAtual = especiesAtuais.get(especieId);
                    if (especieAtual != null) {
                        return especieAtual.getEspecieVegetal().getNomePopular();
                    }
                    return especiesAnteriores.get(especieId).getEspecieVegetal().getNomePopular();
                }))
                .forEach(especieId -> {
                    InventarioEspecie especieAtual = especiesAtuais.get(especieId);
                    InventarioEspecie especieAnterior = especiesAnteriores.get(especieId);

                    Integer quantidadeAtual = especieAtual != null ? especieAtual.getQuantidade() : 0;
                    Integer quantidadeAnterior = especieAnterior != null ? especieAnterior.getQuantidade() : 0;
                    String especieNome = especieAtual != null
                            ? especieAtual.getEspecieVegetal().getNomePopular()
                            : especieAnterior.getEspecieVegetal().getNomePopular();

                    especies.add(new VariacaoEspecieDTO(
                            especieId,
                            especieNome,
                            quantidadeAtual,
                            quantidadeAnterior,
                            quantidadeAtual - quantidadeAnterior
                    ));
                });

        return especies;
    }

    private BigDecimal calcularDapMedio(InventarioFlorestal inventario) {
        List<BigDecimal> daps = inventario.getEspecies().stream()
                .map(InventarioEspecie::getDapMedio)
                .filter(dap -> dap != null)
                .toList();

        if (daps.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal soma = daps.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return soma.divide(BigDecimal.valueOf(daps.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private String calcularTendencia(EstadoGeral anterior, EstadoGeral atual) {
        int valorAnterior = pesoEstado(anterior);
        int valorAtual = pesoEstado(atual);

        if (valorAtual < valorAnterior) {
            return "EVOLUCAO";
        }

        if (valorAtual > valorAnterior) {
            return "DEGRADACAO";
        }

        return "ESTABILIDADE";
    }

    private int pesoEstado(EstadoGeral estado) {
        return switch (estado) {
            case OTIMO -> 1;
            case BOM -> 2;
            case REGULAR -> 3;
            case CRITICO -> 4;
        };
    }

    private void validarAcessoArea(UUID areaId, Authentication auth) {
        if (isAdmin(auth)) {
            return;
        }

        Colaborador colaborador = colaboradorRepository.findByUserUsername(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Colaborador não encontrado para o usuário autenticado"));

        if (colaborador.getAreaAtuacao() == null || !colaborador.getAreaAtuacao().equalsIgnoreCase(areaId.toString())) {
            throw new AccessDeniedException("Acesso negado para esta área");
        }
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}

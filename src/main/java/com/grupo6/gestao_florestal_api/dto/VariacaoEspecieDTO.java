package com.grupo6.gestao_florestal_api.dto;

import java.util.UUID;

public record VariacaoEspecieDTO(
        UUID especieId,
        String especieNome,
        Integer quantidadeAtual,
        Integer quantidadeAnterior,
        Integer variacaoQuantidade
) {
}

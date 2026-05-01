package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;

import java.time.LocalDate;
import java.util.UUID;

public record ColaboradorResponseDTO(
        UUID id,
        String nomeCompleto,
        String cpf,
        String matricula,
        FuncaoColaborador funcao,
        String areaAtuacao,
        LocalDate dataAdmissao,
        String qualificacoes,
        String certificacoes,
        String contatoEmergenciaNome,
        String contatoEmergenciaTel,
        boolean ativo,
        UUID userId
) {
    public static ColaboradorResponseDTO from(Colaborador c) {
        return new ColaboradorResponseDTO(
                c.getId(),
                c.getNomeCompleto(),
                c.getCpf(),
                c.getMatricula(),
                c.getFuncao(),
                c.getAreaAtuacao(),
                c.getDataAdmissao(),
                c.getQualificacoes(),
                c.getCertificacoes(),
                c.getContatoEmergenciaNome(),
                c.getContatoEmergenciaTel(),
                c.isAtivo(),
                c.getUser() != null ? c.getUser().getId() : null
        );
    }
}

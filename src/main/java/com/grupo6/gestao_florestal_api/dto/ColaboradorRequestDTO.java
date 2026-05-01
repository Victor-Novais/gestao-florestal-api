package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ColaboradorRequestDTO(

        @NotBlank(message = "Nome completo e obrigatorio")
        String nomeCompleto,

        @NotBlank(message = "CPF e obrigatorio")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato 000.000.000-00")
        String cpf,

        @NotBlank(message = "Matricula e obrigatoria")
        String matricula,

        @NotNull(message = "Funcao e obrigatoria")
        FuncaoColaborador funcao,

        String areaAtuacao,

        @NotNull(message = "Data de admissao e obrigatoria")
        @PastOrPresent(message = "Data de admissao nao pode ser futura")
        LocalDate dataAdmissao,

        String qualificacoes,

        String certificacoes,

        @NotBlank(message = "Nome do contato de emergencia e obrigatorio")
        String contatoEmergenciaNome,

        @NotBlank(message = "Telefone do contato de emergencia e obrigatorio")
        String contatoEmergenciaTel,

        Boolean criarAcesso,

        @Email(message = "Email de acesso deve ser valido")
        String emailAcesso,

        @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
        String senhaAcesso
) {
}

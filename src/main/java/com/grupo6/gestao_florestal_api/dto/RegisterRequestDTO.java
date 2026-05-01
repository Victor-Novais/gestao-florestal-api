package com.grupo6.gestao_florestal_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank
        @Schema(example = "novo.usuario")
        String username,
        @NotBlank
        @Email
        @Schema(example = "novo.usuario@florestal.com")
        String email,
        @NotBlank
        @Schema(example = "Senha@123")
        String password
) {
}

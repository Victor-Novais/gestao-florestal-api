package com.grupo6.gestao_florestal_api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        @JsonAlias({"username", "email"})
        @Schema(example = "admin@florestal.com")
        String login,
        @NotBlank
        @Schema(example = "admin123")
        String password
) {
}

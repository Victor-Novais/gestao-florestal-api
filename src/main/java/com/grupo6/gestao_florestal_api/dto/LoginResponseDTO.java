package com.grupo6.gestao_florestal_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record LoginResponseDTO(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9")
        String accessToken,
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.refresh")
        String refreshToken,
        @Schema(example = "8640000")
        long expiresIn,
        @Schema(example = "admin")
        String username,
        @Schema(example = "admin@florestal.com")
        String email,
        UUID colaboradorId,
        List<String> roles
) {
}

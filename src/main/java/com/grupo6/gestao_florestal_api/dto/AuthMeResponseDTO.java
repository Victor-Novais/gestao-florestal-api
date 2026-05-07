package com.grupo6.gestao_florestal_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record AuthMeResponseDTO(
        @Schema(example = "admin")
        String username,
        @Schema(example = "admin@florestal.com")
        String email,
        UUID colaboradorId,
        List<String> roles
) {
}

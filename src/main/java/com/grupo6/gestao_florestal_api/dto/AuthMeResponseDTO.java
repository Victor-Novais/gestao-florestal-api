package com.grupo6.gestao_florestal_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AuthMeResponseDTO(
        @Schema(example = "admin")
        String username,
        @Schema(example = "admin@florestal.com")
        String email,
        List<String> roles
) {
}

package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspecieVegetalRequestDTO {

    @NotBlank(message = "Nome científico é obrigatório")
    @Size(max = 255, message = "Nome científico deve ter no máximo 255 caracteres")
    private String nomeCientifico;

    @Size(max = 255, message = "Nome popular deve ter no máximo 255 caracteres")
    private String nomePopular;

    @Size(max = 255, message = "Família botânica deve ter no máximo 255 caracteres")
    private String familiaBotanica;

    @NotNull(message = "Porte é obrigatório")
    private Porte porte;

    @NotNull(message = "Status de conservação é obrigatório")
    private StatusConservacao statusConservacao;

    @Positive(message = "Ciclo de vida deve ser um número positivo")
    private Integer cicloVidaAnos;

    private String exigenciasClimaticas;

    private String exigenciasSolo;

    private Boolean nativa;
}

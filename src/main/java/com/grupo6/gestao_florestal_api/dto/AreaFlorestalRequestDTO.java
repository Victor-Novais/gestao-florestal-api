package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaFlorestalRequestDTO {
    @NotBlank(message = "Identificador único é obrigatório")
    @Size(max = 50, message = "Identificador único deve ter no máximo 50 caracteres")
    private String identificadorUnico;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "Latitude deve ser entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve ser entre -90 e 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude deve ser entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve ser entre -180 e 180")
    private BigDecimal longitude;

    @NotBlank(message = "Município é obrigatório")
    private String municipio;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    @NotNull(message = "Hectares é obrigatório")
    @DecimalMin(value = "0.01", message = "Hectares deve ser maior que 0")
    private BigDecimal hectares;

    @NotNull(message = "Tipo de floresta é obrigatório")
    private TipoFloresta tipoFloresta;

    @NotNull(message = "Bioma é obrigatório")
    private Bioma bioma;

    private StatusArea status;


}

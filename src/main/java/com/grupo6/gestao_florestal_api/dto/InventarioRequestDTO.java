package com.grupo6.gestao_florestal_api.dto;

import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequestDTO {



    @NotBlank(message = "Número da parcela é obrigatório")
    private String numeroParcela;

    @NotNull(message = "ID da área florestal é obrigatório")
    private UUID areaFlorestalId;

    @NotNull(message = "Data da vistoria é obrigatória")
    @PastOrPresent(message = "Data da vistoria não pode ser futura")
    private LocalDate dataVistoria;

    @NotNull(message = "Presença de pragas é obrigatória")
    private Boolean presencaPragas;

    @Size(max = 500, message = "Descrição de pragas deve ter no máximo 500 caracteres")
    private String descricaoPragas;

    @NotNull(message = "Estado geral é obrigatório")
    private EstadoGeral estadoGeral;



    @NotEmpty(message = "Lista de espécies não pode estar vazia")
    @Valid
    private List<InventarioEspecieItemDTO> especies;
}
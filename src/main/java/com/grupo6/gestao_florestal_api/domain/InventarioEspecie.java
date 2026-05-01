package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "inventario_especie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioEspecie {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id", nullable = false)
    private InventarioFlorestal inventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especie_vegetal_id", nullable = false)
    private EspecieVegetal especieVegetal;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "dap_medio", precision = 10, scale = 2)
    private BigDecimal dapMedio;

    @Column(name = "altura_media", precision = 10, scale = 2)
    private BigDecimal alturaMedia;
}
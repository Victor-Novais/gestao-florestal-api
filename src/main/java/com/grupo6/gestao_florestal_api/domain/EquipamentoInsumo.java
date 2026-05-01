package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipamento_insumo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipamentoInsumo {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "codigo_patrimonial", nullable = false, unique = true)
    private String codigoPatrimonial;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaEquipamento categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(name = "unidade_medida", nullable = false)
    private String unidadeMedida;

    @Column(name = "localizacao_atual")
    private String localizacaoAtual;

    @Column(name = "data_aquisicao", nullable = false)
    private LocalDate dataAquisicao;

    // Vida útil em meses
    @Column(name = "vida_util_estimada", nullable = false)
    private Integer vidaUtilEstimada;

    @Builder.Default
    @Column(name = "estoque_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Colaborador responsavel;
}

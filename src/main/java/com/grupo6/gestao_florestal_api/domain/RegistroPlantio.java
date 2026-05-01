package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.MetodoPlantio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registro_plantio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroPlantio {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_florestal_id", nullable = false)
    private AreaFlorestal areaFlorestal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especie_vegetal_id", nullable = false)
    private EspecieVegetal especieVegetal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    @Column(name = "quantidade_mudas", nullable = false)
    private Integer quantidadeMudas;

    @Column(name = "latitude_talhao", precision = 10, scale = 6)
    private BigDecimal latitudeTalhao;

    @Column(name = "longitude_talhao", precision = 10, scale = 6)
    private BigDecimal longitudeTalhao;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatura;

    @Column(precision = 5, scale = 2)
    private BigDecimal umidade;

    @Builder.Default
    @Column(nullable = false)
    private boolean chuva = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_plantio", nullable = false)
    private MetodoPlantio metodoPlantio;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "num_protocolo", nullable = false, unique = true)
    private String numProtocolo;
}

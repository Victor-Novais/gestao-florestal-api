package com.grupo6.gestao_florestal_api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "meta_plantio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetaPlantio {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_florestal_id", nullable = false)
    private AreaFlorestal areaFlorestal;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer ano;

    @Column(name = "quantidade_meta", nullable = false)
    private Integer quantidadeMeta;
}

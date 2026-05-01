package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inventario_florestal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventarioFlorestal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "numero_parcela", nullable = false)
    private String numeroParcela;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_florestal_id", nullable = false)
    private AreaFlorestal areaFlorestal;

    @Column(name = "data_vistoria", nullable = false)
    private LocalDate dataVistoria;

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventarioEspecie> especies = new ArrayList<>();

    @Builder.Default
    @Column(name = "presenca_pragas", nullable = false)
    private Boolean presencaPragas = false;

    @Column(name = "descricao_pragas")
    private String descricaoPragas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_geral", nullable = false)
    private EstadoGeral estadoGeral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }
}

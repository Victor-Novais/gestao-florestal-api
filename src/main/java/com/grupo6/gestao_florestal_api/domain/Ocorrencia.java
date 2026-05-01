package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ocorrencia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ocorrencia {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "numero_protocolo", nullable = false, unique = true)
    private String numeroProtocolo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoOcorrencia tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_florestal_id", nullable = false)
    private AreaFlorestal areaFlorestal;

    @Column(name = "latitude", precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgencia", nullable = false)
    private UrgenciaOcorrencia urgencia;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    @OneToMany(mappedBy = "ocorrencia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OcorrenciaFoto> fotos = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        if (dataRegistro == null) dataRegistro = LocalDateTime.now();
        if (numeroProtocolo == null) {
            numeroProtocolo = gerarProtocolo();
        }
    }

    private String gerarProtocolo() {
        String data = java.time.LocalDate.now().toString().replace("-", "");
        String random = String.format("%06d", new java.util.Random().nextInt(999999));
        return "OC-" + data + "-" + random;
    }
}
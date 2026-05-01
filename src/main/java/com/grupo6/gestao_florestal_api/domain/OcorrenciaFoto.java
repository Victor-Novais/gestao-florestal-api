package com.grupo6.gestao_florestal_api.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "ocorrencia_foto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OcorrenciaFoto {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    private Ocorrencia ocorrencia;

    @Column(name = "url_foto", nullable = false)
    private String urlFoto;
}
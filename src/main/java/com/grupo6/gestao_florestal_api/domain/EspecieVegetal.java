package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "especie_vegetal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecieVegetal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_cientifico", nullable = false, unique = true, length = 255)
    private String nomeCientifico;

    @Column(name = "nome_popular", length = 255)
    private String nomePopular;

    @Column(name = "familia_botanica", length = 255)
    private String familiaBotanica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Porte porte;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_conservacao", nullable = false)
    private StatusConservacao statusConservacao;

    @Column(name = "ciclo_vida_anos")
    private Integer cicloVidaAnos;

    @Column(name = "exigencias_climaticas", columnDefinition = "TEXT")
    private String exigenciasClimaticas;

    @Column(name = "exigencias_solo", columnDefinition = "TEXT")
    private String exigenciasSolo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean nativa = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @PrePersist
    protected void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}

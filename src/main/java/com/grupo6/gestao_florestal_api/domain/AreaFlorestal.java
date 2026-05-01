    package com.grupo6.gestao_florestal_api.domain;

    import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
    import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
    import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;
    import jakarta.persistence.*;
    import lombok.*;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.UUID;

    @Entity
    @Table(name = "area_florestal")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class AreaFlorestal {

        @Id
        @GeneratedValue
        private UUID id;

        @Column(name = "identificador_unico", nullable = false, unique = true)
        private String identificadorUnico;

        @Column(nullable = false)
        private String nome;

        @Column(nullable = false)
        private String municipio;

        @Column(nullable = false, length = 2)
        private String estado;

        @Column(precision = 10, scale = 6)
        private BigDecimal latitude;

        @Column(precision = 10, scale = 6)
        private BigDecimal longitude;

        @Column(name = "tamanho_hectares", nullable = false, precision = 12, scale = 2)
        private BigDecimal hectares;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_floresta", nullable = false)
        private TipoFloresta tipoFloresta;

        @Enumerated(EnumType.STRING)
        @Column(name = "bioma_predominante", nullable = false)
        private Bioma bioma;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private StatusArea status;

        @Builder.Default
        @Column(nullable = false)
        private boolean ativo = true;

        @Column(name = "data_criacao")
        private LocalDateTime criadoEm;

        @Column(name = "data_inativacao")
        private LocalDateTime dataInativacao;

        @PrePersist
        protected void prePersist() {
            if (criadoEm == null) criadoEm = LocalDateTime.now();
            if (status == null) status = StatusArea.ATIVA;
        }
    }

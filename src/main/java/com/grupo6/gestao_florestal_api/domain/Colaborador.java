package com.grupo6.gestao_florestal_api.domain;

import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "colaboradores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colaborador {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuncaoColaborador funcao;

    @Column(name = "area_atuacao")
    private String areaAtuacao;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(columnDefinition = "TEXT")
    private String qualificacoes;

    @Column(columnDefinition = "TEXT")
    private String certificacoes;

    @Column(name = "contato_emergencia_nome", nullable = false)
    private String contatoEmergenciaNome;

    @Column(name = "contato_emergencia_tel", nullable = false)
    private String contatoEmergenciaTel;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

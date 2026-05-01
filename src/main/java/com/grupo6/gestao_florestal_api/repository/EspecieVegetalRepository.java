package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.EspecieVegetal;
import com.grupo6.gestao_florestal_api.domain.enums.Porte;
import com.grupo6.gestao_florestal_api.domain.enums.StatusConservacao;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EspecieVegetalRepository extends JpaRepository<EspecieVegetal, UUID> {

    List<EspecieVegetal> findByStatusConservacao(StatusConservacao statusConservacao);

    List<EspecieVegetal> findByPorte(Porte porte);

    List<EspecieVegetal> findByAtivoTrue();

    List<EspecieVegetal> findByAtivoTrueAndStatusConservacao(StatusConservacao statusConservacao);

    List<EspecieVegetal> findByNativaTrue();

    List<EspecieVegetal> findByNativaFalse();

    Optional<EspecieVegetal> findByNomeCientifico(String nomeCientifico);

    boolean existsByNomeCientifico(String nomeCientifico);

    Page<EspecieVegetal> findByStatusConservacao(StatusConservacao statusConservacao, Pageable pageable);

    Page<EspecieVegetal> findByPorte(Porte porte, Pageable pageable);

    Page<EspecieVegetal> findByAtivoTrue(Pageable pageable);

    Page<EspecieVegetal> findByAtivoTrueAndStatusConservacao(StatusConservacao statusConservacao, Pageable pageable);
}


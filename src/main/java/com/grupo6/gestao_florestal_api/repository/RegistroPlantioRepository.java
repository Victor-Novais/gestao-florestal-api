package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.RegistroPlantio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RegistroPlantioRepository extends JpaRepository<RegistroPlantio, UUID> {

    @Query("""
            SELECT r FROM RegistroPlantio r
            WHERE (:areaId IS NULL OR r.areaFlorestal.id = :areaId)
              AND (:especieId IS NULL OR r.especieVegetal.id = :especieId)
              AND (:colaboradorId IS NULL OR r.colaborador.id = :colaboradorId)
              AND (:inicio IS NULL OR r.dataHora >= :inicio)
              AND (:fim IS NULL OR r.dataHora <= :fim)
            """)
    Page<RegistroPlantio> findWithFilters(
            @Param("areaId") UUID areaId,
            @Param("especieId") UUID especieId,
            @Param("colaboradorId") UUID colaboradorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(r.quantidadeMudas), 0)
            FROM RegistroPlantio r
            WHERE r.colaborador.id = :colaboradorId
              AND YEAR(r.dataHora) = :ano
              AND MONTH(r.dataHora) = :mes
            """)
    Integer sumMudasByColaboradorAndPeriodo(
            @Param("colaboradorId") UUID colaboradorId,
            @Param("ano") int ano,
            @Param("mes") int mes
    );

    @Query("""
            SELECT COALESCE(SUM(r.quantidadeMudas), 0)
            FROM RegistroPlantio r
            WHERE YEAR(r.dataHora) = :ano
              AND MONTH(r.dataHora) = :mes
            """)
    Integer sumMudasAllByPeriodo(
            @Param("ano") int ano,
            @Param("mes") int mes
    );

    @Query("""
            SELECT r
            FROM RegistroPlantio r
            JOIN FETCH r.areaFlorestal
            JOIN FETCH r.especieVegetal
            JOIN FETCH r.colaborador
            WHERE (:inicio IS NULL OR r.dataHora >= :inicio)
              AND (:fim IS NULL OR r.dataHora <= :fim)
            ORDER BY r.dataHora DESC
            """)
    List<RegistroPlantio> buscarProdutividade(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}

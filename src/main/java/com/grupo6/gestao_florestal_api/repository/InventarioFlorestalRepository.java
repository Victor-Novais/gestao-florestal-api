package com.grupo6.gestao_florestal_api.repository;
import com.grupo6.gestao_florestal_api.domain.enums.EstadoGeral;
import com.grupo6.gestao_florestal_api.domain.InventarioFlorestal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface InventarioFlorestalRepository extends JpaRepository<InventarioFlorestal, UUID> {

    @Query("""
        SELECT DISTINCT i
        FROM InventarioFlorestal i
        LEFT JOIN FETCH i.especies ie
        LEFT JOIN FETCH ie.especieVegetal ev
        JOIN FETCH i.areaFlorestal af
        JOIN FETCH i.colaborador c
        WHERE af.id = :areaId
          AND i.numeroParcela = :parcela
          AND (:dataInicio IS NULL OR i.dataVistoria >= :dataInicio)
          AND (:dataFim IS NULL OR i.dataVistoria <= :dataFim)
        ORDER BY i.dataVistoria ASC
    """)
    List<InventarioFlorestal> buscarHistoricoParcela(
            @Param("areaId") UUID areaId,
            @Param("parcela") String parcela,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    Page<InventarioFlorestal> findByDataVistoriaBetween(
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    Page<InventarioFlorestal> findByAreaFlorestalId(UUID areaFlorestalId, Pageable pageable);

    Page<InventarioFlorestal> findByEstadoGeral(EstadoGeral estadoGeral, Pageable pageable);

    Page<InventarioFlorestal> findByColaboradorId(UUID colaboradorId, Pageable pageable);

    @Query("""
        SELECT DISTINCT i
        FROM InventarioFlorestal i
        LEFT JOIN FETCH i.especies ie
        LEFT JOIN FETCH ie.especieVegetal
        JOIN FETCH i.areaFlorestal
        JOIN FETCH i.colaborador
        WHERE (:dataInicio IS NULL OR i.dataVistoria >= :dataInicio)
          AND (:dataFim IS NULL OR i.dataVistoria <= :dataFim)
        ORDER BY i.dataVistoria DESC
    """)
    List<InventarioFlorestal> buscarProdutividade(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

}

package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.AreaFlorestal;
import com.grupo6.gestao_florestal_api.domain.enums.Bioma;
import com.grupo6.gestao_florestal_api.domain.enums.StatusArea;
import com.grupo6.gestao_florestal_api.domain.enums.TipoFloresta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AreaFlorestalRepository extends JpaRepository<AreaFlorestal, UUID> {


    List<AreaFlorestal> findByStatus(StatusArea status);

    List<AreaFlorestal> findByTipoFloresta(TipoFloresta tipoFloresta);

    List<AreaFlorestal> findByBioma(Bioma bioma);

    List<AreaFlorestal> findByAtivoTrue();

    List<AreaFlorestal> findByAtivoTrueAndStatus(StatusArea status);

    Optional<AreaFlorestal> findByIdentificadorUnico(String identificadorUnico);

    boolean existsByIdentificadorUnico(String identificadorUnico);

    Page<AreaFlorestal> findByStatus(StatusArea status, Pageable pageable);

    Page<AreaFlorestal> findByTipoFloresta(TipoFloresta tipoFloresta, Pageable pageable);

    Page<AreaFlorestal> findByBioma(Bioma bioma, Pageable pageable);

    Page<AreaFlorestal> findByAtivoTrue(Pageable pageable);

    @Query("SELECT a.bioma, COUNT(a), SUM(a.hectares) " +
            "FROM AreaFlorestal a WHERE a.ativo = true " +
            "GROUP BY a.bioma")
    List<Object[]> getRelatorioPorBioma();

    @Query(value = """
        SELECT 
            a.bioma_predominante as bioma,
            a.tipo_floresta as tipoFloresta,
            COUNT(a.id) as totalAreas,
            SUM(a.tamanho_hectares) as totalHectares
        FROM area_florestal a
        WHERE a.ativo = true
        AND (CAST(:status AS text) IS NULL OR a.status = CAST(:status AS text))
        GROUP BY a.bioma_predominante, a.tipo_floresta
        ORDER BY a.bioma_predominante, a.tipo_floresta
    """, nativeQuery = true)
    List<Object[]> getRelatorioConsolidadoPorBioma(@Param("status") String status);
}

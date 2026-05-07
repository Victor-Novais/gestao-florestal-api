package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.Ocorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.TipoOcorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, UUID> {

    Page<Ocorrencia> findByTipo(TipoOcorrencia tipo, Pageable pageable);

    Page<Ocorrencia> findByUrgencia(UrgenciaOcorrencia urgencia, Pageable pageable);

    Page<Ocorrencia> findByAreaFlorestalId(UUID areaFlorestalId, Pageable pageable);

    @Query("SELECT o FROM Ocorrencia o WHERE o.dataRegistro BETWEEN :dataInicio AND :dataFim")
    Page<Ocorrencia> findByDataRegistroBetween(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable);

    Page<Ocorrencia> findByColaboradorId(UUID colaboradorId, Pageable pageable);

    @Query("""
            SELECT o
            FROM Ocorrencia o
            JOIN FETCH o.areaFlorestal
            JOIN FETCH o.colaborador
            WHERE (CAST(:inicio AS timestamp) IS NULL OR o.dataRegistro >= :inicio)
              AND (CAST(:fim AS timestamp) IS NULL OR o.dataRegistro <= :fim)
            ORDER BY o.dataRegistro DESC
            """)
    List<Ocorrencia> buscarProdutividade(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    Optional<Ocorrencia> findByNumeroProtocolo(String numeroProtocolo);
}

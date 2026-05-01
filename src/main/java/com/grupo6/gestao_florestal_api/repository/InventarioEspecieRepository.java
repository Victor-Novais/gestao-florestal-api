package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.InventarioEspecie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InventarioEspecieRepository extends JpaRepository<InventarioEspecie,UUID> {

    @Query("SELECT COUNT(DISTINCT ie.inventario.areaFlorestal.id) " +
            "FROM InventarioEspecie ie " +
            "WHERE ie.especieVegetal.id = :especieId")
    Long countAreasPorEspecie(@Param("especieId") UUID especieId);

}

package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.MetaPlantio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetaPlantioRepository extends JpaRepository<MetaPlantio, UUID> {

    Optional<MetaPlantio> findByAreaFlorestal_IdAndMesAndAno(UUID areaId, int mes, int ano);

    List<MetaPlantio> findByMesAndAno(int mes, int ano);
}

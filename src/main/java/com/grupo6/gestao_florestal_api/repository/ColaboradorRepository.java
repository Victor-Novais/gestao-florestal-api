package com.grupo6.gestao_florestal_api.repository;

import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ColaboradorRepository extends JpaRepository<Colaborador, UUID> {

    Optional<Colaborador> findByCpf(String cpf);

    Optional<Colaborador> findByMatricula(String matricula);

    boolean existsByCpf(String cpf);

    boolean existsByMatricula(String matricula);

    @Query("""
            SELECT c FROM Colaborador c
            WHERE (:funcao IS NULL OR c.funcao = :funcao)
              AND (:areaAtuacao IS NULL OR LOWER(c.areaAtuacao) LIKE CONCAT('%', LOWER(CAST(COALESCE(:areaAtuacao, '') AS string)), '%'))
              AND (:ativo IS NULL OR c.ativo = :ativo)
            """)
    Page<Colaborador> findWithFilters(
            @Param("funcao") FuncaoColaborador funcao,
            @Param("areaAtuacao") String areaAtuacao,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );

    Optional<Colaborador> findByUserUsername(String username);

    List<Colaborador> findByAtivoTrueOrderByNomeCompletoAsc();
}

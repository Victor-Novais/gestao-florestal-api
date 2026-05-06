package com.grupo6.gestao_florestal_api.repository;
 
import com.grupo6.gestao_florestal_api.domain.EquipamentoInsumo;
import com.grupo6.gestao_florestal_api.domain.enums.CategoriaEquipamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
public interface EquipamentoInsumoRepository extends JpaRepository<EquipamentoInsumo, UUID> {
 
    boolean existsByCodigoPatrimonial(String codigoPatrimonial);
 
    Optional<EquipamentoInsumo> findByCodigoPatrimonial(String codigoPatrimonial);
 
    // Itens com quantidade <= estoqueMinimo (inclui crítico: quantidade = 0)
    @Query("SELECT e FROM EquipamentoInsumo e WHERE e.ativo = true AND e.quantidade <= e.estoqueMinimo")
    List<EquipamentoInsumo> findAbaixoDoEstoqueMinimo();
 
    // Inventário completo de itens ativos
    @Query("SELECT e FROM EquipamentoInsumo e WHERE e.ativo = true ORDER BY e.descricao ASC")
    List<EquipamentoInsumo> findAllAtivosOrdenados();
 
    // Itens ativos cuja dataAquisicao + vidaUtilEstimada (meses) cai até :dataLimite
    // Ordenado por dataVencimento ASC (diasRestantes crescente)
    @Query("""
            SELECT e FROM EquipamentoInsumo e
            WHERE e.ativo = true
              AND FUNCTION('date_add', e.dataAquisicao, e.vidaUtilEstimada) <= :dataLimite
            ORDER BY FUNCTION('date_add', e.dataAquisicao, e.vidaUtilEstimada) ASC
            """)
    List<EquipamentoInsumo> findProximosAoVencimento(@Param("dataLimite") LocalDate dataLimite);
 
    @Query("""
            SELECT e FROM EquipamentoInsumo e
            WHERE (:categoria IS NULL OR e.categoria = :categoria)
              AND (:localizacao IS NULL OR LOWER(e.localizacaoAtual) LIKE CONCAT('%', LOWER(CAST(COALESCE(:localizacao, '') AS string)), '%'))
              AND (:ativo IS NULL OR e.ativo = :ativo)
            """)
    Page<EquipamentoInsumo> findWithFilters(
            @Param("categoria") CategoriaEquipamento categoria,
            @Param("localizacao") String localizacao,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );
}
 
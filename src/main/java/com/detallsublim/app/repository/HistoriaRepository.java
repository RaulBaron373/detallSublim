package com.detallsublim.app.repository;

import com.detallsublim.app.domain.Historia;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import com.detallsublim.app.service.dto.HistoriaResumenDTO;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Historia entity.
 */
@Repository
public interface HistoriaRepository extends JpaRepository<Historia, Long> {
    @Query(
        value = """
        select new com.detallsublim.app.service.dto.HistoriaResumenDTO(
            h.id,
            h.titulo,
            h.slug,
            h.resumen,
            h.fechaPublicacion,
            i.id,
            i.textoAlternativo
        )
        from Historia h
        left join h.imagenes i on i.portada = true
        where h.estado = :estado
        order by h.fechaPublicacion desc
        """,
        countQuery = """
        select count(h)
        from Historia h
        where h.estado = :estado
        """
    )
    Page<HistoriaResumenDTO> findPublishedSummaries(@Param("estado") EstadoHistoria estado, Pageable pageable);

    @EntityGraph(attributePaths = "imagenes")
    Optional<Historia> findOneWithImagenesBySlugAndEstado(String slug, EstadoHistoria estado);

    @EntityGraph(attributePaths = "imagenes")
    Optional<Historia> findOneWithImagenesById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Historia h where h.id = :id")
    Optional<Historia> findByIdForUpdate(@Param("id") Long id);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}

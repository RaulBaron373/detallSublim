package com.detallsublim.app.repository;

import com.detallsublim.app.domain.HistoriaImagen;
import com.detallsublim.app.domain.enumeration.EstadoHistoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HistoriaImagen entity.
 */
@Repository
public interface HistoriaImagenRepository extends JpaRepository<HistoriaImagen, Long> {
    List<HistoriaImagen> findAllByHistoriaIdOrderByOrdenAsc(Long historiaId);

    long countByHistoriaId(Long historiaId);

    boolean existsByHistoriaIdAndPortadaTrue(Long historiaId);

    Optional<HistoriaImagen> findOneByIdAndHistoriaEstado(Long id, EstadoHistoria estado);
}

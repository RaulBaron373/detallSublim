package com.detallsublim.app.repository;

import com.detallsublim.app.domain.SolicitudPresupuesto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SolicitudPresupuesto entity.
 */
@Repository
public interface SolicitudPresupuestoRepository extends JpaRepository<SolicitudPresupuesto, Long> {
    default Optional<SolicitudPresupuesto> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<SolicitudPresupuesto> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<SolicitudPresupuesto> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select solicitudPresupuesto from SolicitudPresupuesto solicitudPresupuesto left join fetch solicitudPresupuesto.producto",
        countQuery = "select count(solicitudPresupuesto) from SolicitudPresupuesto solicitudPresupuesto"
    )
    Page<SolicitudPresupuesto> findAllWithToOneRelationships(Pageable pageable);

    @Query("select solicitudPresupuesto from SolicitudPresupuesto solicitudPresupuesto left join fetch solicitudPresupuesto.producto")
    List<SolicitudPresupuesto> findAllWithToOneRelationships();

    @Query(
        "select solicitudPresupuesto from SolicitudPresupuesto solicitudPresupuesto left join fetch solicitudPresupuesto.producto where solicitudPresupuesto.id =:id"
    )
    Optional<SolicitudPresupuesto> findOneWithToOneRelationships(@Param("id") Long id);
}

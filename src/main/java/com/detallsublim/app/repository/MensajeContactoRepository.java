package com.detallsublim.app.repository;

import com.detallsublim.app.domain.MensajeContacto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MensajeContacto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {}

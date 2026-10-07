package com.damian.escuela.repositories;

import com.damian.escuela.entities.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    boolean existsByInscripcionId(Long inscripcionId);
}

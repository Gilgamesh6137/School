package com.damian.escuela.repositories;

import com.damian.escuela.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    boolean existsByAlumnoId(Long idAlumno);

    boolean existsByGrupoId(Long grupoId);

    boolean existsByAlumnoIdAndGrupoId(Long idAlumno, Long idGrupo);

    boolean existsByAlumnoIdAndGrupoIdAndIdNot(Long alumnoId, Long grupoId, Long id);
}

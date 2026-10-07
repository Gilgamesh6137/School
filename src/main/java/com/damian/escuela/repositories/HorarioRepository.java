package com.damian.escuela.repositories;

import com.damian.escuela.entities.Horario;
import com.damian.escuela.enums.DiaSemama;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    @Query("""
        SELECT COUNT(h) > 0
        FROM Horario h
        WHERE h.diaSemama = :dia
          AND h.horaInicio < :horaFin
          AND h.horaFin > :horaInicio
          AND h.grupo.periodo = :periodo
          AND (h.grupo.id = :idGrupo OR h.grupo.aula.id = :idAula)
          AND h.id <> :idExcluir
        """)
    boolean existeTraslape(
            @Param("dia") DiaSemama dia,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin,
            @Param("periodo") String periodo,
            @Param("idGrupo") Long idGrupo,
            @Param("idAula") Long idAula,
            @Param("idExcluir") Long idExcluir
    );

    boolean existsByGrupoId(Long grupoId);
}

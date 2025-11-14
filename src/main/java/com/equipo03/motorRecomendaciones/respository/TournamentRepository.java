package com.equipo03.motorRecomendaciones.respository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipo03.motorRecomendaciones.enums.TournamentStatus;
import com.equipo03.motorRecomendaciones.model.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

        Optional<Tournament> findByName(String name);

        Page<Tournament> findByStatusAndGameContainingIgnoreCaseAndNameContainingIgnoreCase(
                        TournamentStatus tournamentStatus, String game, String q, PageRequest pageable);

        Page<Tournament> findByGameContainingIgnoreCaseAndNameContainingIgnoreCase(String game, String q,
                        PageRequest pageable);

        Page<Tournament> findByStatusAndGameContainingIgnoreCase(TournamentStatus tournamentStatus, String game,
                        PageRequest pageable);

        Page<Tournament> findByStatusAndNameContainingIgnoreCase(TournamentStatus tournamentStatus, String q,
                        PageRequest pageable);

        Page<Tournament> findByGameContainingIgnoreCase(String game, PageRequest pageable);

        Page<Tournament> findByNameContainingIgnoreCase(String q, PageRequest pageable);

        Page<Tournament> findByStatus(TournamentStatus tournamentStatus, PageRequest pageable);
}
package com.equipo03.motorRecomendaciones.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipo03.motorRecomendaciones.model.TournamentParticipation;

@Repository
public interface TournamentParticipationRepository extends JpaRepository<TournamentParticipation, Long> {

    boolean existsByTournamentIdAndUserId(Long tournamentId, UUID userId);

    Integer countByTournamentId(Long tournamentId);
}

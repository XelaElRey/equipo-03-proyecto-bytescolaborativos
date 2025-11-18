package com.equipo03.motorRecomendaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.equipo03.motorRecomendaciones.model.enums.TournamentStatus;

@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String game;

	private String rules;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private LocalDateTime registrationOpenAt;

	private LocalDateTime registrationCloseAt;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private TournamentStatus status = TournamentStatus.OPEN;

	private Integer maxParticipants;

	@OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
	private List<TournamentParticipation> participations = new ArrayList<>();
}

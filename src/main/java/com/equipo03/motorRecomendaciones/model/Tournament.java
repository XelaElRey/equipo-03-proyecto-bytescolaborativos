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

	@NotNull(message = "El nombre es obligatorio")
	private String name;

	private String game;

	private String rules;

	@NotNull(message = "La fecha de inicio es obligatoria")
	@Future(message = "La fecha de inicio debe ser futura")
	private LocalDateTime startDate;

	@NotNull(message = "La fecha de fin es obligatoria")
	@Future(message = "La fecha de fin debe ser futura")
	private LocalDateTime endDate;

	@NotNull(message = "La fecha de inicio de inscripción es obligatoria")
	@Future(message = "La fecha de inicio de inscripción debe ser futura")
	private LocalDateTime registrationOpenAt;

	@NotNull(message = "La fecha de cierre de inscripción es obligatoria")
	@Future(message = "La fecha de cierre de inscripción debe ser futura")
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

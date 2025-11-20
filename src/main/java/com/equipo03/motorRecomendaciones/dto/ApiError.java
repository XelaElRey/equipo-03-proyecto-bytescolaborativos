package com.equipo03.motorRecomendaciones.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private int status;
    private String error;
    private String message;
    private List<String> messages;
    private LocalDateTime timestamp;
    private String path;
}

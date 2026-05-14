package com.example.backend.tours;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TourCreateRequest(
    @NotBlank String nombre,
    @NotBlank String descripcion,
    @NotBlank String destinoId,
    @NotNull Double precio,
    @Min(1) int duracionDias,
    @Min(1) int cuposTotal,
    String dificultad,
    String incluye,
    String noIncluye,
    String imagenUrl) {}

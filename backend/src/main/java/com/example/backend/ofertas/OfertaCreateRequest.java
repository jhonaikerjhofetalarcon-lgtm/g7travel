package com.example.backend.ofertas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OfertaCreateRequest(
  @NotBlank String titulo,
  String descripcion,
  @NotNull Integer descuento,
  String fechaInicio,
  String fechaFin,
  String paqueteId) {}

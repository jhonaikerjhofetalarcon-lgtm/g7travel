package com.example.backend.ofertas;

public record OfertaResponse(
  String id,
  String titulo,
  String descripcion,
  int descuento,
  String fechaInicio,
  String fechaFin,
  boolean estado,
  String paqueteId) {

  public static OfertaResponse from(OfertaEntity e) {
    return new OfertaResponse(
      e.getId(),
      e.getTitulo(),
      e.getDescripcion() != null ? e.getDescripcion() : "",
      e.getDescuento() != null ? e.getDescuento() : 0,
      e.getFechaInicio() != null ? e.getFechaInicio() : "",
      e.getFechaFin() != null ? e.getFechaFin() : "",
      Boolean.TRUE.equals(e.getEstado()),
      e.getPaqueteId() != null ? e.getPaqueteId() : "");
  }
}

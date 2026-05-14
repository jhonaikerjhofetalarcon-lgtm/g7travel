package com.example.backend.ofertas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OfertaEntity {

  private String id;

  @NotBlank
  private String titulo;

  private String descripcion;

  @NotNull @Positive
  private Integer descuento;   // porcentaje ej: 20 = 20%
  private String fechaInicio;  // ISO string
  private String fechaFin;     // ISO string
  private Boolean estado;      // true = activa
  private String paqueteId;    // referencia a paquete

  public String getId()                    { return id; }
  public void setId(String id)             { this.id = id; }

  public String getTitulo()                { return titulo; }
  public void setTitulo(String t)          { this.titulo = t; }

  public String getDescripcion()           { return descripcion; }
  public void setDescripcion(String d)     { this.descripcion = d; }

  public Integer getDescuento()            { return descuento; }
  public void setDescuento(Integer d)      { this.descuento = d; }

  public String getFechaInicio()           { return fechaInicio; }
  public void setFechaInicio(String f)     { this.fechaInicio = f; }

  public String getFechaFin()              { return fechaFin; }
  public void setFechaFin(String f)        { this.fechaFin = f; }

  public Boolean getEstado()               { return estado; }
  public void setEstado(Boolean e)         { this.estado = e; }

  public String getPaqueteId()             { return paqueteId; }
  public void setPaqueteId(String p)       { this.paqueteId = p; }
}

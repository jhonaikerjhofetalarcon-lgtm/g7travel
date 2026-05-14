package com.example.backend.tours;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class TourEntity {

  private String id;

  @NotBlank
  private String nombre;

  @NotBlank
  private String descripcion;

  @NotBlank
  private String destinoId;

  @NotNull
  private Double precio;

  @Min(1)
  private int duracionDias;

  @Min(1)
  private int cuposTotal;

  @Min(0)
  private int cuposDisponibles;

  private String dificultad;   // "fácil" | "moderado" | "difícil"
  private String incluye;
  private String noIncluye;
  private String imagenUrl;
  private boolean activo;
  private Instant creadoEn;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

  public String getDestinoId() { return destinoId; }
  public void setDestinoId(String destinoId) { this.destinoId = destinoId; }

  public Double getPrecio() { return precio; }
  public void setPrecio(Double precioNino) { this.precio = precio; }

  public int getDuracionDias() { return duracionDias; }
  public void setDuracionDias(int duracionDias) { this.duracionDias = duracionDias; }

  public int getCuposTotal() { return cuposTotal; }
  public void setCuposTotal(int cuposTotal) { this.cuposTotal = cuposTotal; }

  public int getCuposDisponibles() { return cuposDisponibles; }
  public void setCuposDisponibles(int cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }

  public String getDificultad() { return dificultad; }
  public void setDificultad(String dificultad) { this.dificultad = dificultad; }

  public String getIncluye() { return incluye; }
  public void setIncluye(String incluye) { this.incluye = incluye; }

  public String getNoIncluye() { return noIncluye; }
  public void setNoIncluye(String noIncluye) { this.noIncluye = noIncluye; }

  public String getImagenUrl() { return imagenUrl; }
  public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

  public boolean isActivo() { return activo; }
  public void setActivo(boolean activo) { this.activo = activo; }

  public Instant getCreadoEn() { return creadoEn; }
  public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}

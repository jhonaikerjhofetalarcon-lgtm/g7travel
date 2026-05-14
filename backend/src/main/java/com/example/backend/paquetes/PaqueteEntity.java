package com.example.backend.paquetes;

public class PaqueteEntity {
  private String id;
  private String titulo;
  private String descripcion;
  private Long presio; // Corresponde a int64
  private String id_paquete;
  private String imagenes;
  private Boolean estado;

  // Getters y Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getTitulo() { return titulo; }
  public void setTitulo(String t) { this.titulo = t; }
  public String getDescripcion() { return descripcion; }
  public void setDescripcion(String d) { this.descripcion = d; }
  public Long getPresio() { return presio; }
  public void setPresio(Long p) { this.presio = p; }
  public String getId_paquete() { return id_paquete; }
  public void setId_paquete(String idp) { this.id_paquete = idp; }
  public String getImagenes() { return imagenes; }
  public void setImagenes(String i) { this.imagenes = i; }
  public Boolean getEstado() { return estado; }
  public void setEstado(Boolean e) { this.estado = e; }
}

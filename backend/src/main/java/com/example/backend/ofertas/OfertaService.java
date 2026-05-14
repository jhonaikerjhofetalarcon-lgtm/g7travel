package com.example.backend.ofertas;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OfertaService {

  private final OfertaRepository repository;

  public OfertaService(OfertaRepository repository) {
    this.repository = repository;
  }

  public List<OfertaResponse> getAll() {
    return repository.findAll().stream()
      .map(OfertaResponse::from)
      .toList();
  }

  public List<OfertaResponse> getActivas() {
    return repository.findByEstadoTrue().stream()
      .map(OfertaResponse::from)
      .toList();
  }

  public OfertaResponse getById(String id) {
    return OfertaResponse.from(repository.getOrThrow(id));
  }

  public OfertaResponse create(OfertaCreateRequest request) {
    OfertaEntity entity = new OfertaEntity();
    entity.setId(UUID.randomUUID().toString()); // Generar ID único
    entity.setTitulo(request.titulo());
    entity.setDescripcion(request.descripcion());
    entity.setDescuento(request.descuento());
    entity.setFechaInicio(request.fechaInicio());
    entity.setFechaFin(request.fechaFin());
    entity.setPaqueteId(request.paqueteId());
    entity.setEstado(true); // Por defecto activa al crear

    return OfertaResponse.from(repository.save(entity));
  }

  public OfertaResponse update(String id, OfertaCreateRequest request) {
    OfertaEntity entity = repository.getOrThrow(id);

    entity.setTitulo(request.titulo());
    entity.setDescripcion(request.descripcion());
    entity.setDescuento(request.descuento());
    entity.setFechaInicio(request.fechaInicio());
    entity.setFechaFin(request.fechaFin());
    entity.setPaqueteId(request.paqueteId());

    return OfertaResponse.from(repository.save(entity));
  }

  public void delete(String id) {
    if (!repository.existsById(id)) {
      throw new java.util.NoSuchElementException("No se puede eliminar: Oferta no encontrada");
    }
    repository.deleteById(id);
  }

  public OfertaResponse toggleEstado(String id) {
    OfertaEntity entity = repository.getOrThrow(id);
    entity.setEstado(!Boolean.TRUE.equals(entity.getEstado()));
    return OfertaResponse.from(repository.save(entity));
  }
}

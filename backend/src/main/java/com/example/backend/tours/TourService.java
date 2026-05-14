package com.example.backend.tours;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TourService {

  private final TourRepository repo;

  public TourService(TourRepository repo) {
    this.repo = repo;
  }

  public List<TourResponse> list() {
    return repo.findAll().stream().map(TourResponse::from).toList();
  }

  public List<TourResponse> listByDestino(String destinoId) {
    return repo.findByDestinoId(destinoId).stream().map(TourResponse::from).toList();
  }

  public TourResponse get(String id) {
    return TourResponse.from(repo.getOrThrow(id));
  }

  public TourResponse create(TourCreateRequest req) {
    TourEntity e = new TourEntity();
    e.setId(UUID.randomUUID().toString());
    e.setNombre(req.nombre());
    e.setDescripcion(req.descripcion());
    e.setDestinoId(req.destinoId());
    e.setPrecio(req.precio());
    e.setDuracionDias(req.duracionDias());
    e.setCuposTotal(req.cuposTotal());
    e.setCuposDisponibles(req.cuposTotal());
    e.setDificultad(req.dificultad());
    e.setIncluye(req.incluye());
    e.setNoIncluye(req.noIncluye());
    e.setImagenUrl(req.imagenUrl());
    e.setActivo(true);
    e.setCreadoEn(Instant.now());
    return TourResponse.from(repo.save(e));
  }

  public TourResponse update(String id, TourCreateRequest req) {
    TourEntity e = repo.getOrThrow(id);
    e.setNombre(req.nombre());
    e.setDescripcion(req.descripcion());
    e.setDestinoId(req.destinoId());
    e.setPrecio(req.precio());
    e.setDuracionDias(req.duracionDias());
    e.setCuposTotal(req.cuposTotal());
    e.setDificultad(req.dificultad());
    e.setIncluye(req.incluye());
    e.setNoIncluye(req.noIncluye());
    e.setImagenUrl(req.imagenUrl());
    return TourResponse.from(repo.save(e));
  }

  public void delete(String id) {
    if (!repo.existsById(id)) throw new NoSuchElementException("Tour no encontrado");
    repo.deleteById(id);
  }
}

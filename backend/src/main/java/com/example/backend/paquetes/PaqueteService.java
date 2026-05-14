package com.example.backend.paquetes;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaqueteService {
  private final PaqueteRepository repository;

  public PaqueteService(PaqueteRepository repository) {
    this.repository = repository;
  }

  public List<PaqueteResponse> getAll() {
    return repository.findAll().stream().map(PaqueteResponse::from).toList();
  }

  public PaqueteResponse create(PaqueteCreateRequest req) {
    PaqueteEntity e = new PaqueteEntity();
    e.setId(UUID.randomUUID().toString());
    e.setTitulo(req.titulo());
    e.setDescripcion(req.descripcion());
    e.setPresio(req.presio());
    e.setId_paquete(req.id_paquete());
    e.setImagenes(req.imagenes());
    e.setEstado(false); // Por defecto false según tu requerimiento
    return PaqueteResponse.from(repository.save(e));
  }

  public void delete(String id) {
    repository.deleteById(id);
  }
}

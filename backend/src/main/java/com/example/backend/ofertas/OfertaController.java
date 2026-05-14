package com.example.backend.ofertas;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ofertas")
public class OfertaController {

  private final OfertaService service;

  public OfertaController(OfertaService service) {
    this.service = service;
  }

  @GetMapping
  public List<OfertaResponse> listarTodas() {
    return service.getAll();
  }

  @GetMapping("/activas")
  public List<OfertaResponse> listarActivas() {
    return service.getActivas();
  }

  @GetMapping("/{id}")
  public OfertaResponse obtenerPorId(@PathVariable String id) {
    return service.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OfertaResponse crear(@RequestBody @Valid OfertaCreateRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public OfertaResponse actualizar(@PathVariable String id, @RequestBody @Valid OfertaCreateRequest request) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/toggle")
  public OfertaResponse cambiarEstado(@PathVariable String id) {
    return service.toggleEstado(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable String id) {
    service.delete(id);
  }
}

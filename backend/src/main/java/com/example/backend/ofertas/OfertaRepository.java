package com.example.backend.ofertas;

import com.example.backend.firestore.FirestoreCollections;
import com.example.backend.firestore.FirestoreFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OfertaRepository {

  private final Firestore db;

  public OfertaRepository(Firestore db) { this.db = db; }

  private com.google.cloud.firestore.CollectionReference col() {
    return db.collection(FirestoreCollections.OFERTAS);
  }

  public List<OfertaEntity> findAll() {
    return FirestoreFutures.get(col().get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<OfertaEntity> findByEstadoTrue() {
    return FirestoreFutures.get(col().whereEqualTo("estado", true).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<OfertaEntity> findByPaqueteId(String paqueteId) {
    return FirestoreFutures.get(col().whereEqualTo("paqueteId", paqueteId).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<OfertaEntity> findByEstadoTrueAndPaqueteId(String paqueteId) {
    return FirestoreFutures.get(
        col().whereEqualTo("estado", true).whereEqualTo("paqueteId", paqueteId).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public Optional<OfertaEntity> findById(String id) {
    DocumentSnapshot doc = FirestoreFutures.get(col().document(id).get());
    return doc.exists() ? Optional.of(toEntity(doc)) : Optional.empty();
  }

  public OfertaEntity getOrThrow(String id) {
    return findById(id).orElseThrow(() -> new NoSuchElementException("Oferta no encontrada: " + id));
  }

  public OfertaEntity save(OfertaEntity e) {
    Map<String, Object> data = new HashMap<>();
    data.put("titulo",      e.getTitulo());
    data.put("descripcion", e.getDescripcion() != null ? e.getDescripcion() : "");
    data.put("descuento",   e.getDescuento() != null ? e.getDescuento() : 0);
    data.put("fechaInicio", e.getFechaInicio() != null ? e.getFechaInicio() : "");
    data.put("fechaFin",    e.getFechaFin() != null ? e.getFechaFin() : "");
    data.put("estado",      Boolean.TRUE.equals(e.getEstado()));
    data.put("paqueteId",   e.getPaqueteId() != null ? e.getPaqueteId() : "");
    FirestoreFutures.get(col().document(e.getId()).set(data));
    return e;
  }

  public void deleteById(String id) {
    FirestoreFutures.get(col().document(id).delete());
  }

  public boolean existsById(String id) {
    return FirestoreFutures.get(col().document(id).get()).exists();
  }

  public long count() {
    return FirestoreFutures.get(col().get()).size();
  }

  private OfertaEntity toEntity(DocumentSnapshot d) {
    OfertaEntity e = new OfertaEntity();
    e.setId(d.getId());
    e.setTitulo(safeStr(d, "titulo"));
    e.setDescripcion(safeStr(d, "descripcion"));
    e.setDescuento(safeInt(d, "descuento"));
    e.setFechaInicio(safeStr(d, "fechaInicio"));
    e.setFechaFin(safeStr(d, "fechaFin"));
    e.setEstado(Boolean.TRUE.equals(d.getBoolean("estado")));
    e.setPaqueteId(safeStr(d, "paqueteId"));
    return e;
  }

  private String safeStr(DocumentSnapshot d, String f) {
    Object v = d.get(f); return v == null ? "" : String.valueOf(v);
  }

  private int safeInt(DocumentSnapshot d, String f) {
    Object v = d.get(f);
    if (v instanceof Number n) return n.intValue();
    if (v instanceof String s) { try { return Integer.parseInt(s); } catch (Exception ignored) {} }
    return 0;
  }
}

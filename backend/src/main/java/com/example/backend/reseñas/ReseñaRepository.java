package com.example.backend.reseñas;

import com.example.backend.firestore.FirestoreCollections;
import com.example.backend.firestore.FirestoreFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Repository;

@Repository
public class ReseñaRepository {

  private final Firestore db;

  public ReseñaRepository(Firestore db) {
    this.db = db;
  }

  public List<ReseñaEntity> findAll() {
    return FirestoreFutures.get(db.collection(FirestoreCollections.RESEÑAS).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<ReseñaEntity> findByTourId(String tourId) {
    return FirestoreFutures.get(
        db.collection(FirestoreCollections.RESEÑAS)
          .whereEqualTo("tourId", tourId).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public ReseñaEntity getOrThrow(String id) {
    var doc = FirestoreFutures.get(
      db.collection(FirestoreCollections.RESEÑAS).document(id).get());
    if (!doc.exists()) throw new NoSuchElementException("Reseña no encontrada: " + id);
    return toEntity(doc);
  }

  public ReseñaEntity save(ReseñaEntity e) {
    Map<String, Object> data = new HashMap<>();
    data.put("tourId",      e.getTourId() != null ? e.getTourId() : "");
    data.put("clienteId",   e.getClienteId() != null ? e.getClienteId() : "");
    data.put("calificacion", e.getCalificacion());
    data.put("titulo",      e.getTitulo() != null ? e.getTitulo() : "");
    data.put("comentario",  e.getComentario() != null ? e.getComentario() : "");
    data.put("verificada",  e.isVerificada());

    data.put("fecha", e.getFecha() != null ? e.getFecha().toString() : Instant.now().toString());

    FirestoreFutures.get(
      db.collection(FirestoreCollections.RESEÑAS).document(e.getId()).set(data));
    return e;
  }

  public void deleteById(String id) {
    FirestoreFutures.get(
      db.collection(FirestoreCollections.RESEÑAS).document(id).delete());
  }

  public boolean existsById(String id) {
    return FirestoreFutures.get(
      db.collection(FirestoreCollections.RESEÑAS).document(id).get()).exists();
  }

  private ReseñaEntity toEntity(DocumentSnapshot doc) {
    ReseñaEntity e = new ReseñaEntity();
    e.setId(doc.getId());
    e.setTourId(safeStr(doc, "tourId"));
    e.setClienteId(safeStr(doc, "clienteId"));
    e.setCalificacion(safeInt(doc, "calificacion"));
    e.setTitulo(safeStr(doc, "titulo"));
    e.setComentario(safeStr(doc, "comentario"));
    e.setVerificada(Boolean.TRUE.equals(doc.getBoolean("verificada")));
    e.setFecha(safeInstant(doc, "fecha"));
    return e;
  }

  private String safeStr(DocumentSnapshot d, String field) {
    Object v = d.get(field);
    return v == null ? "" : String.valueOf(v);
  }

  private int safeInt(DocumentSnapshot d, String field) {
    Object v = d.get(field);
    if (v instanceof Number n) return n.intValue();
    if (v instanceof String s) { try { return Integer.parseInt(s); } catch (Exception ignored) {} }
    return 0;
  }

  private Instant safeInstant(DocumentSnapshot d, String field) {
    Object v = d.get(field);
    if (v instanceof com.google.cloud.Timestamp ts)
      return ts.toDate().toInstant();
    if (v instanceof Map<?, ?> map) {
      Object secs = map.get("seconds");
      Object nano  = map.get("nanos");
      if (secs instanceof Number n)
        return Instant.ofEpochSecond(n.longValue(),
          nano instanceof Number nn ? nn.longValue() : 0);
    }
    if (v instanceof String s && !s.isBlank()) {
      try { return Instant.parse(s); } catch (Exception ignored) {}
    }
    return Instant.EPOCH;
  }
}

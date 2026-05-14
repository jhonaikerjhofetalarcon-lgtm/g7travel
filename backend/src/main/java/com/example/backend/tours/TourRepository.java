package com.example.backend.tours;

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
public class TourRepository {

  private final Firestore db;

  public TourRepository(Firestore db) {
    this.db = db;
  }

  public List<TourEntity> findAll() {
    return FirestoreFutures.get(db.collection(FirestoreCollections.TOURS).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<TourEntity> findByDestinoId(String destinoId) {
    return FirestoreFutures.get(
        db.collection(FirestoreCollections.TOURS)
          .whereEqualTo("destinoId", destinoId).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public TourEntity getOrThrow(String id) {
    var doc = FirestoreFutures.get(
      db.collection(FirestoreCollections.TOURS).document(id).get());
    if (!doc.exists()) throw new NoSuchElementException("Tour no encontrado: " + id);
    return toEntity(doc);
  }

  public TourEntity save(TourEntity e) {
    Map<String, Object> data = new HashMap<>();
    data.put("nombre",           e.getNombre());
    data.put("descripcion",      e.getDescripcion());
    data.put("destinoId",        e.getDestinoId());
    data.put("precio",           e.getPrecio());
    data.put("duracionDias",     e.getDuracionDias());
    data.put("cuposTotal",       e.getCuposTotal());
    data.put("cuposDisponibles", e.getCuposDisponibles());
    data.put("dificultad",       e.getDificultad());
    data.put("incluye",          e.getIncluye() != null ? e.getIncluye() : "");
    data.put("noIncluye",        e.getNoIncluye() != null ? e.getNoIncluye() : "");
    data.put("imagenUrl",        e.getImagenUrl() != null ? e.getImagenUrl() : "");
    data.put("activo",           e.isActivo());
    // Guardamos como String ISO para evitar el bug de HashMap con Timestamp
    data.put("creadoEn", e.getCreadoEn() != null ? e.getCreadoEn().toString() : Instant.now().toString());

    FirestoreFutures.get(
      db.collection(FirestoreCollections.TOURS).document(e.getId()).set(data));
    return e;
  }

  public void deleteById(String id) {
    FirestoreFutures.get(
      db.collection(FirestoreCollections.TOURS).document(id).delete());
  }

  public boolean existsById(String id) {
    return FirestoreFutures.get(
      db.collection(FirestoreCollections.TOURS).document(id).get()).exists();
  }

  public long count() {
    return FirestoreFutures.get(db.collection(FirestoreCollections.TOURS).get()).size();
  }

  private TourEntity toEntity(DocumentSnapshot doc) {
    TourEntity e = new TourEntity();
    e.setId(doc.getId());
    e.setNombre(safeStr(doc, "nombre"));
    e.setDescripcion(safeStr(doc, "descripcion"));
    e.setDestinoId(safeStr(doc, "destinoId"));
    e.setPrecio(safeDouble(doc, "precio"));
    e.setDuracionDias(safeInt(doc, "duracionDias"));
    e.setCuposTotal(safeInt(doc, "cuposTotal"));
    e.setCuposDisponibles(safeInt(doc, "cuposDisponibles"));
    e.setDificultad(safeStr(doc, "dificultad"));
    e.setIncluye(safeStr(doc, "incluye"));
    e.setNoIncluye(safeStr(doc, "noIncluye"));
    e.setImagenUrl(safeStr(doc, "imagenUrl"));
    e.setActivo(Boolean.TRUE.equals(doc.getBoolean("activo")));
    e.setCreadoEn(safeInstant(doc, "creadoEn"));
    return e;
  }

  private String safeStr(DocumentSnapshot d, String field) {
    Object v = d.get(field);
    return v == null ? "" : String.valueOf(v);
  }

  private double safeDouble(DocumentSnapshot d, String field) {
    Object v = d.get(field);
    if (v instanceof Number n) return n.doubleValue();
    if (v instanceof String s) { try { return Double.parseDouble(s); } catch (Exception ignored) {} }
    return 0.0;
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
      Object nano = map.get("nanos");
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

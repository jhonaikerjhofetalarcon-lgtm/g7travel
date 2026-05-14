package com.example.backend.pagos;

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
public class PagoRepository {

  private final Firestore db;

  public PagoRepository(Firestore db) {
    this.db = db;
  }

  public List<PagoEntity> findAll() {
    return FirestoreFutures.get(db.collection(FirestoreCollections.PAGOS).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<PagoEntity> findByReservaId(String reservaId) {
    return FirestoreFutures.get(
        db.collection(FirestoreCollections.PAGOS)
          .whereEqualTo("reservaId", reservaId).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public PagoEntity getOrThrow(String id) {
    var doc = FirestoreFutures.get(
      db.collection(FirestoreCollections.PAGOS).document(id).get());
    if (!doc.exists()) throw new NoSuchElementException("Pago no encontrado: " + id);
    return toEntity(doc);
  }

  public PagoEntity save(PagoEntity e) {
    Map<String, Object> data = new HashMap<>();
    data.put("reservaId",      e.getReservaId() != null ? e.getReservaId() : "");
    data.put("monto",          e.getMonto() != null ? e.getMonto() : 0.0);
    data.put("metodo",         e.getMetodo() != null ? e.getMetodo() : "");
    data.put("estado",         e.getEstado() != null ? e.getEstado() : "pendiente");
    data.put("referencia",     e.getReferencia() != null ? e.getReferencia() : "");
    data.put("comprobanteUrl", e.getComprobanteUrl() != null ? e.getComprobanteUrl() : "");
    // Guardamos como String ISO para evitar el bug HashMap con Timestamp
    data.put("fechaPago", e.getFechaPago() != null ? e.getFechaPago().toString() : Instant.now().toString());

    FirestoreFutures.get(
      db.collection(FirestoreCollections.PAGOS).document(e.getId()).set(data));
    return e;
  }

  public void deleteById(String id) {
    FirestoreFutures.get(
      db.collection(FirestoreCollections.PAGOS).document(id).delete());
  }

  public boolean existsById(String id) {
    return FirestoreFutures.get(
      db.collection(FirestoreCollections.PAGOS).document(id).get()).exists();
  }

  private PagoEntity toEntity(DocumentSnapshot doc) {
    PagoEntity e = new PagoEntity();
    e.setId(doc.getId());
    e.setReservaId(safeStr(doc, "reservaId"));
    e.setMonto(safeDouble(doc, "monto"));
    e.setMetodo(safeStr(doc, "metodo"));
    e.setEstado(safeStr(doc, "estado"));
    e.setReferencia(safeStr(doc, "referencia"));
    e.setComprobanteUrl(safeStr(doc, "comprobanteUrl"));
    e.setFechaPago(safeInstant(doc, "fechaPago"));
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

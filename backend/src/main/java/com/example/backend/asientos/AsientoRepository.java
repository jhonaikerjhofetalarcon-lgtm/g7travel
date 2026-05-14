package com.example.backend.asientos;

import com.example.backend.firestore.FirestoreCollections;
import com.example.backend.firestore.FirestoreFutures;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Repository;

@Repository
public class AsientoRepository {

  private final Firestore db;

  public AsientoRepository(Firestore db) {
    this.db = db;
  }

  public List<AsientoEntity> findAll() {
    return FirestoreFutures.get(db.collection(FirestoreCollections.ASIENTOS).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<AsientoEntity> findByIdAuto(String idAuto) {
    return FirestoreFutures.get(
        db.collection(FirestoreCollections.ASIENTOS)
          .whereEqualTo("idAuto", idAuto).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<AsientoEntity> findByEstado(String estado) {
    return FirestoreFutures.get(
        db.collection(FirestoreCollections.ASIENTOS)
          .whereEqualTo("estado", estado).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public List<AsientoEntity> findByIdAutoAndEstado(String idAuto, String estado) {
    return FirestoreFutures.get(
        db.collection(FirestoreCollections.ASIENTOS)
          .whereEqualTo("idAuto", idAuto)
          .whereEqualTo("estado", estado).get())
      .getDocuments().stream().map(this::toEntity).toList();
  }

  public AsientoEntity getOrThrow(String id) {
    var doc = FirestoreFutures.get(
      db.collection(FirestoreCollections.ASIENTOS).document(id).get());
    if (!doc.exists()) throw new NoSuchElementException("Asiento no encontrado: " + id);
    return toEntity(doc);
  }

  public AsientoEntity save(AsientoEntity e) {
    FirestoreFutures.get(
      db.collection(FirestoreCollections.ASIENTOS).document(e.getId()).set(e));
    return e;
  }

  public void deleteById(String id) {
    FirestoreFutures.get(
      db.collection(FirestoreCollections.ASIENTOS).document(id).delete());
  }

  public boolean existsById(String id) {
    return FirestoreFutures.get(
      db.collection(FirestoreCollections.ASIENTOS).document(id).get()).exists();
  }

  public long count() {
    return FirestoreFutures.get(db.collection(FirestoreCollections.ASIENTOS).get()).size();
  }

  private AsientoEntity toEntity(DocumentSnapshot doc) {
    AsientoEntity e = new AsientoEntity();
    e.setId(doc.getId());
    e.setIdAuto(doc.getString("idAuto"));
    e.setIdReserva(doc.getString("idReserva"));
    e.setNumeroAsiento(doc.getString("numeroAsiento"));
    e.setEstado(doc.getString("estado"));
    return e;
  }
}

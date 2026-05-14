package com.example.backend.seed;

import com.example.backend.destinos.DestinoEntity;
import com.example.backend.destinos.DestinoRepository;
import com.example.backend.tours.TourEntity;
import com.example.backend.tours.TourRepository;
import com.example.backend.users.UserEntity;
import com.example.backend.users.UserRepository;
import com.example.backend.users.UserRole;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final UserRepository    userRepo;
  private final DestinoRepository destinoRepo;
  private final TourRepository    tourRepo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public DataInitializer(UserRepository userRepo, DestinoRepository destinoRepo, TourRepository tourRepo) {
    this.userRepo    = userRepo;
    this.destinoRepo = destinoRepo;
    this.tourRepo    = tourRepo;
  }

  @Override
  public void run(String... args) {
    seedUsers();
    seedDestinos();
    seedTours();
  }

  // ── Users ─────────────────────────────────────────────
  private void seedUsers() {
    if (userRepo.count() > 0) return;
    for (UserEntity u : List.of(
      user("Carlos Quispe",  "carlos@g7travel.com",  "987 654 321", UserRole.admin,     "admin1"),
      user("María Huamán",   "maria@g7travel.com",   "976 543 210", UserRole.conductor, "pass01"),
      user("Luis Flores",    "luis@g7travel.com",    "965 432 109", UserRole.conductor, "pass02"),
      user("Ana Torres",     "ana@g7travel.com",     "954 321 098", UserRole.conductor, "pass03"),
      user("Pedro Mendoza",  "pedro@g7travel.com",   "943 210 987", UserRole.admin,     "admin2"))) {
      userRepo.save(u);
    }
  }

  private UserEntity user(String nombre, String email, String telefono, UserRole rol, String password) {
    UserEntity e = new UserEntity();
    e.setId(UUID.randomUUID().toString());
    e.setNombre(nombre);
    e.setEmail(email);
    e.setTelefono(telefono);
    e.setRol(rol);
    e.setPasswordHash(encoder.encode(password));
    return e;
  }

  // ── Destinos ──────────────────────────────────────────
  private void seedDestinos() {
    if (destinoRepo.count() > 0) return;
    for (DestinoEntity d : List.of(
      destino("histórica", "Pampa de Quinua",
        "Ubicada cerca de Ayacucho, paisaje andino de gran valor histórico y natural.",
        "Historia Viva",
        "https://www.ytuqueplanes.com/imagenes/fotos/novedades/sierra-pampa-quinua.JPG",
        "https://www.ytuqueplanes.com/imagenes/fotos/novedades/sierra-pampa-quinua.JPG"),
      destino("Mística", "Machu Picchu",
        "Antigua ciudad inca rodeada de montañas. Una de las maravillas del mundo.",
        "Maravilla",
        "https://images.pexels.com/photos/259967/pexels-photo-259967.jpeg",
        "https://images.pexels.com/photos/259967/pexels-photo-259967.jpeg"),
      destino("Histórico", "Cusco",
        "Ciudad andina llena de historia, antigua capital del Imperio Inca.",
        "Milenario",
        "https://images.pexels.com/photos/21014/pexels-photo.jpg",
        "https://images.pexels.com/photos/21014/pexels-photo.jpg"),
      destino("Místico", "Lago Titicaca",
        "El lago navegable más alto del mundo, rodeado de tradiciones ancestrales.",
        "Sagrado",
        "https://images.pexels.com/photos/753626/pexels-photo-753626.jpeg",
        "https://images.pexels.com/photos/753626/pexels-photo-753626.jpeg"),
      destino("Tradicional", "Sarhua",
        "Pueblo andino conocido por su arte tradicional y las tablas de Sarhua.",
        "Cultural",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT4qHUBk0VGAdXrmJiwT98ksH9f9KWWm7Sicw&s",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT4qHUBk0VGAdXrmJiwT98ksH9f9KWWm7Sicw&s"))) {
      destinoRepo.save(d);
    }
  }

  private DestinoEntity destino(String label, String title, String desc, String name, String bg, String thumb) {
    DestinoEntity d = new DestinoEntity();
    d.setId(UUID.randomUUID().toString());
    d.setLabel(label); d.setTitle(title); d.setDesc(desc);
    d.setName(name);   d.setBg(bg);       d.setThumb(thumb);
    return d;
  }

  // ── Tours ─────────────────────────────────────────────
  private void seedTours() {
    if (tourRepo.count() > 0) return;
    List<DestinoEntity> destinos = destinoRepo.findAll();
    if (destinos.isEmpty()) return;

    String idMachu = destinos.stream().filter(d -> d.getTitle().contains("Machu")).map(DestinoEntity::getId).findFirst().orElse(destinos.get(0).getId());
    String idCusco = destinos.stream().filter(d -> d.getTitle().contains("Cusco")).map(DestinoEntity::getId).findFirst().orElse(destinos.get(0).getId());
    String idTiti  = destinos.stream().filter(d -> d.getTitle().contains("Titicaca")).map(DestinoEntity::getId).findFirst().orElse(destinos.get(0).getId());
    String idAyacu = destinos.stream().filter(d -> d.getTitle().contains("Quinua")).map(DestinoEntity::getId).findFirst().orElse(destinos.get(0).getId());

    for (TourEntity t : List.of(
      tour("Machu Picchu Clásico", "Visita la ciudadela inca más famosa del mundo con guía especializado.",
        idMachu, 450.0, 3, 20, "moderado", "Transporte, hotel 2 noches, guía, entrada", "Comidas, vuelos",
        "https://images.pexels.com/photos/259967/pexels-photo-259967.jpeg"),
      tour("Cusco Imperial", "Recorre el Qorikancha, Sacsayhuamán y el mercado de San Pedro.",
        idCusco, 320.0, 2, 15, "fácil", "Transporte, hotel 1 noche, guía, entradas", "Comidas, souvenirs",
        "https://images.pexels.com/photos/21014/pexels-photo.jpg"),
      tour("Lago Titicaca Ancestral", "Navega por el lago más alto del mundo y visita las islas flotantes.",
        idTiti, 380.0, 2, 12, "fácil", "Transporte, bote, hotel, guía", "Comidas personales",
        "https://images.pexels.com/photos/753626/pexels-photo-753626.jpeg"),
      tour("Ayacucho Historia Viva", "Descubre la Pampa de Quinua, escenario de la Batalla de Ayacucho.",
        idAyacu, 250.0, 1, 25, "fácil", "Transporte, guía local, almuerzo típico", "Gastos personales",
        "https://www.ytuqueplanes.com/imagenes/fotos/novedades/sierra-pampa-quinua.JPG"))) {
      tourRepo.save(t);
    }
  }

  private TourEntity tour(String nombre, String desc, String destinoId,
                          double precio, int dias, int cupos,
                          String dificultad, String incluye, String noIncluye, String imagenUrl) {
    TourEntity t = new TourEntity();
    t.setId(UUID.randomUUID().toString());
    t.setNombre(nombre);
    t.setDescripcion(desc);
    t.setDestinoId(destinoId);
    t.setPrecio(precio);
    t.setDuracionDias(dias);
    t.setCuposTotal(cupos);
    t.setCuposDisponibles(cupos);
    t.setDificultad(dificultad);
    t.setIncluye(incluye);
    t.setNoIncluye(noIncluye);
    t.setImagenUrl(imagenUrl);
    t.setActivo(true);
    t.setCreadoEn(Instant.now());
    return t;
  }
}

package py.edu.uc.lp3.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.domain.ResultadoExplosion;
import py.edu.uc.lp3.domain.Sonoro;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;
import py.edu.uc.lp3.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.domain.exception.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.exception.ReglaMinecraftException;
import py.edu.uc.lp3.domain.exception.VidaInvalidaException;

@Service
public class EntidadService {

    private static final int RADIO_EXPLOSION = 3;
    private final EntidadRepository repository;

    public EntidadService(EntidadRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Entidad> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Entidad buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<SonidoEntidad> listarSonidos() {
        return repository.findByVidaGreaterThan(0).stream()
                .filter(entidad -> entidad instanceof Sonoro)
                .map(entidad -> {
                    Sonoro emisor = (Sonoro) entidad;
                    return new SonidoEntidad(entidad.getId(), entidad.tipo(), emisor.emitirSonido());
                })
                .toList();
    }

    @Transactional
    public Entidad crear(String tipo, Integer vida, Integer posicionX, Integer posicionZ) {
        validarCampos(tipo, vida, posicionX, posicionZ);
        Entidad entidad = switch (tipo.trim().toLowerCase(Locale.ROOT)) {
            case "zombie", "creeper" -> crearMob(tipo, vida, posicionX, posicionZ);
            case "cerdo" -> new Cerdo(validarVida(vida, Cerdo.VIDA_MAXIMA), posicionX, posicionZ);
            default -> throw new ReglaMinecraftException("Tipo no soportado. Use Zombie, Creeper o Cerdo");
        };
        return repository.save(entidad);
    }

    @Transactional
    public Entidad actualizar(Long id, String tipo, Integer vida, Integer posicionX, Integer posicionZ) {
        Entidad entidad = buscar(id);
        exigirActiva(entidad);
        validarCampos(tipo, vida, posicionX, posicionZ);
        if (!entidad.tipo().equalsIgnoreCase(tipo.trim())) {
            throw new ReglaMinecraftException("No se puede cambiar el tipo de una entidad existente");
        }
        validarVida(vida, entidad.estado().vidaMaxima());
        entidad.actualizarEstado(vida, posicionX, posicionZ);
        return repository.save(entidad);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional
    public ResultadoExplosion explotar(Long id) {
        Entidad entidad = buscar(id);
        if (!(entidad instanceof Creeper creeper)) {
            throw new ExplosionInvalidaException("Solo un Creeper puede provocar una explosión");
        }
        return detonarSobre(creeper, repository.findByVidaGreaterThan(0));
    }

    public Mob crearMob(String tipo, int vida, int x, int z) {
        return switch (tipo.trim().toLowerCase(Locale.ROOT)) {
            case "zombie" -> new Zombie(validarVida(vida, Zombie.VIDA_MAXIMA), x, z);
            case "creeper" -> new Creeper(validarVida(vida, Creeper.VIDA_MAXIMA), x, z);
            default -> throw new IllegalArgumentException("Tipo de mob no soportado: " + tipo);
        };
    }

    private int validarVida(int vida, int maxima) {
        if (vida < 1 || vida > maxima) {
            throw new VidaInvalidaException("La vida debe estar entre 1 y " + maxima);
        }
        return vida;
    }

    private void exigirActiva(Entidad entidad) {
        if (!entidad.estaViva()) {
            throw new EntidadDesaparecidaException();
        }
    }

    public String sonido(Entidad entidad) {
        exigirActiva(entidad);
        return entidad.sonido();
    }

    public void mover(Entidad entidad, int dx, int dz) {
        exigirActiva(entidad);
        if (dx == 0 && dz == 0) {
            throw new IllegalArgumentException("El desplazamiento no puede ser cero");
        }
        if (entidad instanceof Mob && Math.abs((long) dx) + Math.abs((long) dz) > 8) {
            throw new IllegalArgumentException("Un mob no puede desplazarse más de 8 bloques");
        }
        var estado = entidad.estado();
        try {
            entidad.actualizarEstado(estado.vida(), Math.addExact(estado.posicionX(), dx),
                    Math.addExact(estado.posicionZ(), dz));
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("La posición excede el mundo permitido", e);
        }
    }

    public void recibirDanio(Entidad entidad, int cantidad) {
        recibirDanio(entidad, cantidad, 0);
    }

    public void recibirDanio(Entidad entidad, int cantidad, int proteccion) {
        exigirActiva(entidad);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("El daño debe ser positivo");
        }
        if (proteccion < 0) {
            throw new IllegalArgumentException("La protección no puede ser negativa");
        }
        var estado = entidad.estado();
        int vida = Math.max(0, estado.vida() - Math.max(0, cantidad - proteccion));
        entidad.actualizarEstado(vida, estado.posicionX(), estado.posicionZ());
        if (vida == 0 && entidad instanceof Creeper creeper) {
            creeper.descargar();
        }
    }

    public String atacar(Mob mob) {
        exigirActiva(mob);
        if (mob instanceof Creeper creeper) {
            creeper.cargar();
        }
        return mob.atacar();
    }

    public String consumir(Creeper creeper) {
        exigirActiva(creeper);
        if (!creeper.estaCargado()) {
            throw new ExplosionInvalidaException("El Creeper debe cargar antes de explotar");
        }
        creeper.actualizarEstado(0, creeper.posicion().x(), creeper.posicion().z());
        creeper.descargar();
        return "El Creeper explotó y desapareció";
    }

    ResultadoExplosion detonarSobre(Creeper creeper, List<? extends Entidad> entidades) {
        atacar(creeper);
        var afectados = new ArrayList<ResultadoExplosion.Impacto>();
        for (Entidad objetivo : entidades) {
            if (!(objetivo instanceof Zombie || objetivo instanceof Cerdo) || !objetivo.estaViva()) {
                continue;
            }
            double distancia = Math.hypot(
                    (double) objetivo.posicion().x() - creeper.posicion().x(),
                    (double) objetivo.posicion().z() - creeper.posicion().z());
            if (distancia <= RADIO_EXPLOSION) {
                int anterior = objetivo.estado().vida();
                recibirDanio(objetivo, 12 - 3 * (int) Math.floor(distancia));
                int actual = objetivo.estado().vida();
                afectados.add(new ResultadoExplosion.Impacto(objetivo.getId(), objetivo.tipo(),
                        distancia, anterior - actual, anterior, actual, objetivo.estaViva()));
            }
        }
        consumir(creeper);
        return new ResultadoExplosion(creeper.getId(), creeper.estado(), RADIO_EXPLOSION, afectados);
    }

    private void validarCampos(String tipo, Integer vida, Integer posicionX, Integer posicionZ) {
        if (tipo == null || tipo.isBlank()) {
            throw new ReglaMinecraftException("El tipo es obligatorio");
        }
        if (vida == null || posicionX == null || posicionZ == null) {
            throw new ReglaMinecraftException("La vida y las posiciones X y Z son obligatorias");
        }
        if (vida <= 0) {
            throw new VidaInvalidaException("La vida debe ser positiva");
        }
    }

    public record SonidoEntidad(Long id, String tipo, String mensaje) {
    }
}

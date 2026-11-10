package py.edu.uc.lp3.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.ResultadoExplosion;
import py.edu.uc.lp3.domain.Sonoro;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.repository.EntidadRepository;

@Service
@Transactional(readOnly = true)
public class EntidadService {

    private final EntidadRepository repository;

    public EntidadService(EntidadRepository repository) {
        this.repository = repository;
    }

    public List<Entidad> listar() {
        return repository.findAll();
    }

    public Entidad buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la entidad con id " + id));
    }

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
            case "zombie" -> new Zombie(vida, posicionX, posicionZ);
            case "creeper" -> new Creeper(vida, posicionX, posicionZ);
            case "cerdo" -> new Cerdo(vida, posicionX, posicionZ);
            default -> throw new IllegalArgumentException("Tipo no soportado. Use Zombie, Creeper o Cerdo");
        };
        return repository.save(entidad);
    }

    @Transactional
    public Entidad actualizar(Long id, String tipo, Integer vida, Integer posicionX, Integer posicionZ) {
        Entidad entidad = buscar(id);
        validarCampos(tipo, vida, posicionX, posicionZ);
        if (!entidad.tipo().equalsIgnoreCase(tipo.trim())) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de una entidad existente");
        }
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
            throw new IllegalArgumentException("Solo un Creeper puede provocar una explosion");
        }
        // Las entidades estan administradas por JPA; sus cambios se guardan al confirmar la transaccion.
        return creeper.detonarSobre(repository.findByVidaGreaterThan(0));
    }

    private void validarCampos(String tipo, Integer vida, Integer posicionX, Integer posicionZ) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        if (vida == null || posicionX == null || posicionZ == null) {
            throw new IllegalArgumentException("La vida y las posiciones X y Z son obligatorias");
        }
        if (vida <= 0) {
            throw new IllegalArgumentException("La vida debe ser positiva");
        }
    }

    public record SonidoEntidad(Long id, String tipo, String mensaje) {
    }
}

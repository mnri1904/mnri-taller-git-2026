package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.ResultadoExplosion;
import py.edu.uc.lp3.service.EntidadService;

@RestController
@RequestMapping("/api/minecraft/entidades")
public class EntidadController {

    private final EntidadService service;

    public EntidadController(EntidadService service) {
        this.service = service;
    }

    @GetMapping
    public List<RespuestaEntidad> listar() {
        return service.listar().stream().map(RespuestaEntidad::desde).toList();
    }

    @GetMapping("/{id}")
    public RespuestaEntidad buscar(@PathVariable Long id) {
        return RespuestaEntidad.desde(service.buscar(id));
    }

    @GetMapping("/sonidos")
    public List<EntidadService.SonidoEntidad> listarSonidos() {
        return service.listarSonidos();
    }

    @PostMapping
    public ResponseEntity<RespuestaEntidad> crear(@RequestBody SolicitudEntidad solicitud) {
        Entidad entidad = service.crear(solicitud.tipo(), solicitud.vida(),
                solicitud.posicionX(), solicitud.posicionZ());
        return ResponseEntity.created(URI.create("/api/minecraft/entidades/" + entidad.getId()))
                .body(RespuestaEntidad.desde(entidad));
    }

    @PutMapping("/{id}")
    public RespuestaEntidad actualizar(@PathVariable Long id, @RequestBody SolicitudEntidad solicitud) {
        return RespuestaEntidad.desde(service.actualizar(id, solicitud.tipo(), solicitud.vida(),
                solicitud.posicionX(), solicitud.posicionZ()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/explosion")
    public ResultadoExplosion explotar(@PathVariable Long id) {
        return service.explotar(id);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarValidacion(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    public record SolicitudEntidad(String tipo, Integer vida, Integer posicionX, Integer posicionZ) {
    }

    public record RespuestaEntidad(Long id, String tipo, int vida, int vidaMaxima,
            int posicionX, int posicionZ, boolean viva) {

        public static RespuestaEntidad desde(Entidad entidad) {
            var estado = entidad.estado();
            return new RespuestaEntidad(entidad.getId(), estado.tipo(), estado.vida(), estado.vidaMaxima(),
                    estado.posicionX(), estado.posicionZ(), estado.viva());
        }
    }
}

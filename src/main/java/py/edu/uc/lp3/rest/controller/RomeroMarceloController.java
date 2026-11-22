package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.EstadoEntidad;
import py.edu.uc.lp3.service.EntidadService;

@RestController
@RequestMapping("/api/minecraft/creeper")
public class RomeroMarceloController {

    private Creeper creeper = new Creeper();
    private final EntidadService service;

    public RomeroMarceloController(EntidadService service) {
        this.service = service;
    }

    @GetMapping
    public synchronized EstadoCreeper consultar() {
        return estadoActual();
    }

    @PostMapping("/mover")
    public synchronized EstadoCreeper mover(@RequestBody Movimiento movimiento) {
        service.mover(creeper, movimiento.x(), movimiento.z());
        return estadoActual();
    }

    @PostMapping("/danio")
    public synchronized EstadoCreeper recibirDanio(
            @RequestBody Danio danio,
            @RequestParam(required = false) Integer proteccion) {
        if (proteccion == null) {
            service.recibirDanio(creeper, danio.cantidad());
        } else {
            service.recibirDanio(creeper, danio.cantidad(), proteccion);
        }
        return estadoActual();
    }

    @PostMapping("/cargar")
    public synchronized Respuesta atacar() {
        return new Respuesta(service.atacar(creeper), estadoActual());
    }

    @PostMapping("/explotar")
    public synchronized Respuesta explotar() {
        return new Respuesta(service.consumir(creeper), estadoActual());
    }

    @PostMapping("/reiniciar")
    public synchronized EstadoCreeper reiniciar() {
        creeper = new Creeper();
        return estadoActual();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarReglaInvalida(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    private EstadoCreeper estadoActual() {
        return new EstadoCreeper(creeper.estado(), creeper.estaCargado());
    }

    public record Movimiento(int x, int z) {
    }

    public record Danio(int cantidad) {
    }

    public record EstadoCreeper(EstadoEntidad entidad, boolean cargado) {
    }

    public record Respuesta(String mensaje, EstadoCreeper estado) {
    }
}

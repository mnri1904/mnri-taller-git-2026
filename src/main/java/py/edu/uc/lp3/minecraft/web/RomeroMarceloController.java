package py.edu.uc.lp3.minecraft.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.minecraft.EstadoEntidad;

@RestController
@RequestMapping("/api/minecraft/creeper")
public class RomeroMarceloController {

    private Creeper creeper = new Creeper();

    @GetMapping
    public synchronized EstadoCreeper consultar() {
        return estadoActual();
    }

    @PostMapping("/mover")
    public synchronized EstadoCreeper mover(@RequestBody Movimiento movimiento) {
        creeper.mover(movimiento.x(), movimiento.z());
        return estadoActual();
    }

    @PostMapping("/danio")
    public synchronized EstadoCreeper recibirDanio(@RequestBody Danio danio) {
        creeper.recibirDanio(danio.cantidad());
        return estadoActual();
    }

    @PostMapping("/cargar")
    public synchronized Respuesta atacar() {
        return new Respuesta(creeper.atacar(), estadoActual());
    }

    @PostMapping("/explotar")
    public synchronized Respuesta explotar() {
        return new Respuesta(creeper.explotar(), estadoActual());
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

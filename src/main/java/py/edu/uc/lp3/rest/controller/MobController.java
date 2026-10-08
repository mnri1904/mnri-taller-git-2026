package py.edu.uc.lp3.rest.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.EstadoEntidad;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.domain.Zombie;

@RestController
@RequestMapping("/api/minecraft/mobs")
public class MobController {

    @GetMapping("/{tipo}")
    public RespuestaMob crear(
            @PathVariable String tipo,
            @RequestParam int x,
            @RequestParam int z,
            @RequestParam(defaultValue = "20") int vida) {
        Mob mob = crearMob(tipo, vida, x, z);
        return new RespuestaMob(mob.estado(), mob.atacar());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarReglaInvalida(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    private Mob crearMob(String tipo, int vida, int x, int z) {
        return switch (tipo.toLowerCase(Locale.ROOT)) {
            case "creeper" -> new Creeper(vida, x, z);
            case "zombie" -> new Zombie(vida, x, z);
            default -> throw new IllegalArgumentException("Tipo de mob no soportado: " + tipo);
        };
    }

    public record RespuestaMob(EstadoEntidad entidad, String comportamiento) {
    }
}

package py.edu.uc.lp3.minecraft.web;

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

import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.minecraft.EstadoEntidad;
import py.edu.uc.lp3.minecraft.Mob;
import py.edu.uc.lp3.minecraft.Zombie;

@RestController
@RequestMapping("/api/minecraft/mobs")
public class MobController {

    @GetMapping("/{tipo}")
    public RespuestaMob crear(
            @PathVariable String tipo,
            @RequestParam int x,
            @RequestParam int z) {
        Mob mob = crearMob(tipo);
        if (x != 0 || z != 0) {
            mob.mover(x, z);
        }
        return new RespuestaMob(mob.estado(), mob.atacar());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarReglaInvalida(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    private Mob crearMob(String tipo) {
        return switch (tipo.toLowerCase(Locale.ROOT)) {
            case "creeper" -> new Creeper();
            case "zombie" -> new Zombie();
            default -> throw new IllegalArgumentException("Tipo de mob no soportado: " + tipo);
        };
    }

    public record RespuestaMob(EstadoEntidad entidad, String comportamiento) {
    }
}

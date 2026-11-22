package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.EstadoEntidad;
import py.edu.uc.lp3.domain.Mob;
import py.edu.uc.lp3.service.EntidadService;

@RestController
@RequestMapping("/api/minecraft/mobs")
public class MobController {

    private final EntidadService service;

    public MobController(EntidadService service) {
        this.service = service;
    }

    @GetMapping("/{tipo}")
    public RespuestaMob crear(
            @PathVariable String tipo,
            @RequestParam int x,
            @RequestParam int z,
            @RequestParam(defaultValue = "20") int vida) {
        Mob mob = service.crearMob(tipo, vida, x, z);
        return new RespuestaMob(mob.estado(), service.atacar(mob));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarReglaInvalida(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    public record RespuestaMob(EstadoEntidad entidad, String comportamiento) {
    }
}

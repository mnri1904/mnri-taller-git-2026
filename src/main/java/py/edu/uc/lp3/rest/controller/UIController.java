package py.edu.uc.lp3.rest.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.PosicionUI;
import py.edu.uc.lp3.domain.UICapable;
import py.edu.uc.lp3.domain.Zombie;

@RestController
@RequestMapping("/api/minecraft/ui")
public class UIController {

    @GetMapping(value = "/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespuestaUI obtenerUI(@PathVariable String tipo) {
        UICapable dibujable = crearDibujable(tipo.toLowerCase(Locale.ROOT));
        return new RespuestaUI(dibujable.imagen(), dibujable.posicion(),
                dibujable.sonido(), dibujable.velocidad());
    }

    private UICapable crearDibujable(String tipo) {
        return switch (tipo) {
            case "creeper" -> new Creeper();
            case "zombie" -> new Zombie();
            case "cerdo" -> new Cerdo();
            default -> throw new IllegalArgumentException("Tipo dibujable no soportado: " + tipo);
        };
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarTipoInvalido(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    public record RespuestaUI(String imagen, PosicionUI posicion, String sonido, double velocidad) {
    }
}

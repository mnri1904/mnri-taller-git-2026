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
import py.edu.uc.lp3.domain.Sonoro;
import py.edu.uc.lp3.domain.Zombie;

@RestController
@RequestMapping("/api/minecraft/sonidos")
public class SonidoController {

    @GetMapping(value = "/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RespuestaSonido obtenerSonido(@PathVariable String tipo) {
        String tipoNormalizado = tipo.toLowerCase(Locale.ROOT);
        Sonoro emisor = crearEmisor(tipoNormalizado);
        return new RespuestaSonido(tipoNormalizado, emisor.emitirSonido());
    }

    private Sonoro crearEmisor(String tipo) {
        return switch (tipo) {
            case "cerdo" -> new Cerdo();
            case "zombie" -> new Zombie();
            default -> throw new IllegalArgumentException("Tipo sonoro no soportado: " + tipo);
        };
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarTipoInvalido(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    public record RespuestaSonido(String tipo, String mensaje) {
    }
}

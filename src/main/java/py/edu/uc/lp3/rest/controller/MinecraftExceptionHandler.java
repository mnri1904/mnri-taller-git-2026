package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;
import py.edu.uc.lp3.domain.exception.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.exception.ReglaMinecraftException;
import py.edu.uc.lp3.domain.exception.VidaInvalidaException;

@RestControllerAdvice
public class MinecraftExceptionHandler {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrada(EntidadNoEncontradaException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("codigo", "ENTIDAD_NO_ENCONTRADA", "error", exception.getMessage()));
    }

    @ExceptionHandler(VidaInvalidaException.class)
    public ResponseEntity<Map<String, String>> manejarVidaInvalida(VidaInvalidaException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("codigo", "VIDA_INVALIDA", "error", exception.getMessage()));
    }

    @ExceptionHandler(EntidadDesaparecidaException.class)
    public ResponseEntity<Map<String, String>> manejarDesaparecida(EntidadDesaparecidaException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("codigo", "ENTIDAD_DESAPARECIDA", "error", exception.getMessage()));
    }

    @ExceptionHandler(ExplosionInvalidaException.class)
    public ResponseEntity<Map<String, String>> manejarExplosionInvalida(ExplosionInvalidaException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("codigo", "EXPLOSION_INVALIDA", "error", exception.getMessage()));
    }

    @ExceptionHandler(ReglaMinecraftException.class)
    public ResponseEntity<Map<String, String>> manejarRegla(ReglaMinecraftException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("codigo", "REGLA_MINECRAFT_INVALIDA", "error", exception.getMessage()));
    }
}

package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import py.edu.uc.lp3.cliente.ClienteAlexandre;
import py.edu.uc.lp3.cliente.ClienteElias;
import py.edu.uc.lp3.domain.JugadaAldea;
import py.edu.uc.lp3.service.EscenarioAldeaService;

@RestController
@Tag(name = "Escenario de la aldea")
@RequestMapping(EscenarioAldeaController.RUTA)
public class EscenarioAldeaController {
    static final String RUTA = "/api/minecraft/jugadas/aldea";
    private final EscenarioAldeaService servicio;
    private final ObjectMapper json;

    public EscenarioAldeaController(EscenarioAldeaService servicio, ObjectMapper json) {
        this.servicio = servicio;
        this.json = json;
    }

    public record PedidoAldea(
            @Schema(example = "10") Integer potencia, @Schema(example = "3") Integer radio,
            @Schema(example = "0,64,0") String posicionCreeper, @Schema(example = "3") Integer tiempoCarga,
            @Schema(example = "1") Long creeperEliasId, @Schema(example = "2") Long zombieEliasId,
            @Schema(example = "1") Integer distancia) {}

    public record ErrorEscenario(String origen, String codigo, String mensaje) {}

    @PostMapping
    @Operation(summary = "Detonar cerca de la aldea", description = "Usar IDs de Elías. A3 resuelve la onda primero. "
            + "Si acepta, A2 resuelve el ataque. Solo dos aceptaciones insertan una jugada en A1. "
            + "Ante un rechazo de A2, el daño aplicado en A3 permanece.")
    @ApiResponse(responseCode = "201", description = "Jugada guardada. Consultar la ruta de Location.", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Pedido incompleto en A1 o rechazo de un compañero, como potencia cero en A3.",
            content = @Content(schema = @Schema(implementation = ErrorEscenario.class)))
    @ApiResponse(responseCode = "404", description = "El compañero no encontró la entidad indicada.",
            content = @Content(schema = @Schema(implementation = ErrorEscenario.class)))
    @ApiResponse(responseCode = "422", description = "Rechazo de Elías, por ejemplo FUERA_DE_ALCANCE.",
            content = @Content(schema = @Schema(implementation = ErrorEscenario.class)))
    @ApiResponse(responseCode = "502", description = "Fallo de comunicación con el compañero.",
            content = @Content(schema = @Schema(implementation = ErrorEscenario.class)))
    public ResponseEntity<JugadaAldea> ejecutar(@RequestBody PedidoAldea p) {
        if (p.potencia() == null || p.radio() == null || p.posicionCreeper() == null || p.tiempoCarga() == null
                || p.creeperEliasId() == null || p.zombieEliasId() == null || p.distancia() == null) {
            throw new PedidoIncompleto();
        }
        var jugada = servicio.ejecutar(new ClienteAlexandre.Solicitud(p.potencia(), p.radio(), p.posicionCreeper(), p.tiempoCarga()),
                p.creeperEliasId(), new ClienteElias.Solicitud(p.zombieEliasId(), p.distancia()));
        return ResponseEntity.created(URI.create(RUTA + "/" + jugada.getId())).body(jugada);
    }

    @GetMapping
    @Operation(summary = "Listar las jugadas guardadas en H2 de Marcelo")
    public List<JugadaAldea> listar() { return servicio.listar(); }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una jugada guardada por su ID de A1")
    public ResponseEntity<JugadaAldea> buscar(@PathVariable long id) { return ResponseEntity.of(servicio.buscar(id)); }

    private static class PedidoIncompleto extends RuntimeException {}

    @ExceptionHandler({PedidoIncompleto.class, org.springframework.http.converter.HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorEscenario> pedidoInvalido() {
        return ResponseEntity.badRequest().body(new ErrorEscenario("A1 Marcelo", "PEDIDO_INVALIDO",
                "Enviar los siete campos del escenario con los tipos indicados."));
    }

    @ExceptionHandler(EscenarioAldeaService.FalloCompanero.class)
    public ResponseEntity<ErrorEscenario> fallo(EscenarioAldeaService.FalloCompanero fallo) {
        if (fallo.getCause() instanceof HttpClientErrorException rechazo) {
            String codigo = "RECHAZO_COMPANERO";
            String mensaje = "El compañero rechazó el pedido.";
            try {
                JsonNode cuerpo = json.readTree(rechazo.getResponseBodyAsString());
                if (cuerpo != null) {
                    codigo = cuerpo.path("codigo").asText(cuerpo.path("tipo").asText(codigo));
                    mensaje = cuerpo.path("mensaje").asText(mensaje);
                }
            } catch (JsonProcessingException ignorado) {
                // Conservar el estado y el origen aunque el cuerpo no sea JSON.
            }
            return ResponseEntity.status(rechazo.getStatusCode()).body(new ErrorEscenario(fallo.getOrigen(), codigo, mensaje));
        }
        return ResponseEntity.status(502).body(new ErrorEscenario(fallo.getOrigen(), "FALLO_COMPANERO",
                "No se obtuvo una respuesta válida del compañero."));
    }
}

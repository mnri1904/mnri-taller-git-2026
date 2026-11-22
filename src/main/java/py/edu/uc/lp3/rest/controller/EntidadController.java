package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Entidades", description = "CRUD, sonidos y simulación persistida")
@RequestMapping("/api/minecraft/entidades")
public class EntidadController {

    private final EntidadService service;

    public EntidadController(EntidadService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "listarEntidades", summary = "Listar todas las entidades",
            description = "Lectura de H2 que incluye las entidades desaparecidas con vida cero.")
    public List<RespuestaEntidad> listar() {
        return service.listar().stream().map(RespuestaEntidad::desde).toList();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "consultarEntidad", summary = "Leer el estado guardado por ID",
            description = "Usar el ID recibido al crear la entidad. Una entidad desaparecida todavía se puede consultar.")
    @ApiResponse(responseCode = "200", description = "Entidad encontrada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", ref = "#/components/responses/NoEncontrada")
    public RespuestaEntidad buscar(@PathVariable Long id) {
        return RespuestaEntidad.desde(service.buscar(id));
    }

    @GetMapping("/sonidos")
    @Operation(operationId = "listarSonidos", summary = "Consultar los sonidos de Zombies y Cerdos vivos",
            description = "Lectura sin efectos en H2. Si no hay emisores, devuelve una lista vacía.")
    public List<EntidadService.SonidoEntidad> listarSonidos() {
        return service.listarSonidos();
    }

    @PostMapping
    @Operation(operationId = "crearEntidad", summary = "Crear una entidad con ID nuevo",
            description = "Inserta una fila en H2. En una base vacía, ejecutar los tres ejemplos válidos "
                    + "para preparar la simulación y guardar sus IDs.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SolicitudEntidad.class),
                            examples = {
                                    @ExampleObject(name = "Creeper", value = "{\"tipo\":\"Creeper\",\"vida\":20,\"posicionX\":5,\"posicionZ\":5}"),
                                    @ExampleObject(name = "Zombie", value = "{\"tipo\":\"Zombie\",\"vida\":20,\"posicionX\":6,\"posicionZ\":5}"),
                                    @ExampleObject(name = "Cerdo", value = "{\"tipo\":\"Cerdo\",\"vida\":10,\"posicionX\":7,\"posicionZ\":5}"),
                                    @ExampleObject(name = "Rechazado: Cerdo con vida 11", value = "{\"tipo\":\"Cerdo\",\"vida\":11,\"posicionX\":7,\"posicionZ\":5}")
                            })))
    @ApiResponse(responseCode = "201", description = "Entidad creada", useReturnTypeSchema = true,
            headers = @Header(name = "Location", description = "Ruta de la entidad creada", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", description = "VIDA_INVALIDA (vida fuera del rango) o REGLA_MINECRAFT_INVALIDA (tipo o campos). No se inserta ninguna fila.",
            content = @Content(schema = @Schema(implementation = ErrorJuego.class),
                    examples = @ExampleObject(value = "{\"codigo\":\"VIDA_INVALIDA\",\"error\":\"La vida debe estar entre 1 y 10\"}")))
    public ResponseEntity<RespuestaEntidad> crear(@RequestBody SolicitudEntidad solicitud) {
        Entidad entidad = service.crear(solicitud.tipo(), solicitud.vida(),
                solicitud.posicionX(), solicitud.posicionZ());
        return ResponseEntity.created(URI.create("/api/minecraft/entidades/" + entidad.getId()))
                .body(RespuestaEntidad.desde(entidad));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "actualizarEntidad", summary = "Actualizar una entidad activa",
            description = "Enviar los cuatro campos, incluido el tipo original. Cambia vida y posición en H2; "
                    + "no cambia el ID ni el tipo. Un rechazo deja la fila intacta.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "ENTIDAD_DESAPARECIDA, VIDA_INVALIDA o REGLA_MINECRAFT_INVALIDA.",
            content = @Content(schema = @Schema(implementation = ErrorJuego.class)))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/NoEncontrada")
    public RespuestaEntidad actualizar(@PathVariable Long id, @RequestBody SolicitudEntidad solicitud) {
        return RespuestaEntidad.desde(service.actualizar(id, solicitud.tipo(), solicitud.vida(),
                solicitud.posicionX(), solicitud.posicionZ()));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "eliminarEntidad", summary = "Eliminar definitivamente una fila de H2",
            description = "A diferencia de la desaparición en el juego, el borrado hace que el siguiente GET devuelva 404.")
    @ApiResponse(responseCode = "204", description = "Fila eliminada; sin cuerpo", content = @Content)
    @ApiResponse(responseCode = "404", ref = "#/components/responses/NoEncontrada")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/explosion")
    @Operation(operationId = "detonarCreeper", summary = "Detonar y consumir un Creeper activo",
            description = "Sin cuerpo: usar el ID del Creeper. Regla 1: los Zombies y Cerdos vivos a distancia "
                    + "euclídea d ≤ 3 en X/Z reciben 12 − 3 × floor(d) de daño, sin bajar de vida cero. "
                    + "Regla 2: el Creeper se consume aun sin objetivos y no puede revivir ni explotar otra vez. "
                    + "Guarda todos los cambios en H2 en una transacción. Con los tres ejemplos válidos de creación, "
                    + "un GET posterior muestra vidas 0, 11 y 4. Para probar rechazos, repetir con el mismo ID "
                    + "o usar el ID del Zombie.")
    @ApiResponse(responseCode = "200", description = "Explosión guardada", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Rechazo sin daño ni cambios en H2.",
            content = @Content(schema = @Schema(implementation = ErrorJuego.class), examples = {
                    @ExampleObject(name = "Segundo intento", value = "{\"codigo\":\"ENTIDAD_DESAPARECIDA\",\"error\":\"La entidad ya desapareció\"}"),
                    @ExampleObject(name = "ID de Zombie o Cerdo", value = "{\"codigo\":\"EXPLOSION_INVALIDA\",\"error\":\"Solo un Creeper puede provocar una explosión\"}")
            }))
    @ApiResponse(responseCode = "404", ref = "#/components/responses/NoEncontrada")
    public ResultadoExplosion explotar(@PathVariable Long id) {
        return service.explotar(id);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarValidacion(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @Schema(requiredProperties = {"tipo", "vida", "posicionX", "posicionZ"})
    public record SolicitudEntidad(
            @Schema(description = "Creeper, Zombie o Cerdo; se ignoran las mayúsculas y los espacios exteriores.", example = "Creeper") String tipo,
            @Schema(description = "Hasta 10 para Cerdo y 20 para Creeper o Zombie.", minimum = "1", maximum = "20", example = "20") Integer vida,
            @Schema(example = "5") Integer posicionX, @Schema(example = "5") Integer posicionZ) {
    }

    @Schema(description = "Rechazo de una regla o ID inexistente.", requiredProperties = {"codigo", "error"})
    public record ErrorJuego(String codigo, String error) {
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

package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import py.edu.uc.lp3.cliente.ClienteAlexandre;
import py.edu.uc.lp3.cliente.ClienteElias;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EscenarioAldeaHttpTest {
    @Autowired TestRestTemplate http;
    @MockitoBean ClienteAlexandre alexandre;
    @MockitoBean ClienteElias elias;
    private final String ruta = "/api/minecraft/jugadas/aldea";
    private final Map<String, Object> pedido = Map.of("potencia", 10, "radio", 3, "posicionCreeper", "0,64,0",
            "tiempoCarga", 3, "creeperEliasId", 1, "zombieEliasId", 2, "distancia", 1);

    @Test
    void dosAceptacionesDevuelvenUnaUbicacionQueSePuedeConsultar() {
        when(alexandre.simularExplosion(any())).thenReturn(new ClienteAlexandre.Resultado(10, 3, "0,64,0", 0, List.of()));
        when(elias.detonar(eq(1L), any())).thenReturn(new ClienteElias.Resultado(9, null,
                new ClienteElias.Hostil(2, "ZOMBIE", "Sombra", 11, 3, true)));
        var creado = http.postForEntity(ruta, pedido, JsonNode.class);
        assertThat(creado.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = creado.getHeaders().getLocation();
        assertThat(location.getPath()).isEqualTo(ruta + "/" + creado.getBody().get("id").asLong());
        assertThat(http.getForObject(location.getPath(), JsonNode.class)).isEqualTo(creado.getBody());
    }

    @Test
    void rechazosConservanOrigenCodigoYMensajeSinNuevaFila() {
        var antes = http.getForObject(ruta, JsonNode.class);
        when(alexandre.simularExplosion(any())).thenThrow(rechazo(HttpStatus.BAD_REQUEST, "tipo", "REGLA_DE_JUEGO"))
                .thenReturn(new ClienteAlexandre.Resultado(10, 3, "0,64,0", 0, List.of()));
        var a3 = http.postForEntity(ruta, pedido, JsonNode.class);
        assertThat(a3.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(a3.getBody().path("origen").asText()).isEqualTo("A3 Alexandre");
        assertThat(a3.getBody().path("codigo").asText()).isEqualTo("REGLA_DE_JUEGO");
        verifyNoInteractions(elias);
        when(elias.detonar(eq(1L), any())).thenThrow(rechazo(HttpStatus.UNPROCESSABLE_ENTITY, "codigo", "FUERA_DE_ALCANCE"));
        var a2 = http.postForEntity(ruta, pedido, JsonNode.class);
        assertThat(a2.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(a2.getBody().path("origen").asText()).isEqualTo("A2 Elías");
        assertThat(a2.getBody().path("codigo").asText()).isEqualTo("FUERA_DE_ALCANCE");
        assertThat(a2.getBody().path("mensaje").asText()).isEqualTo("Regla rechazada.");
        assertThat(http.getForObject(ruta, JsonNode.class)).isEqualTo(antes);
    }

    private HttpClientErrorException rechazo(HttpStatus estado, String campo, String codigo) {
        var json = "{\"" + campo + "\":\"" + codigo + "\",\"mensaje\":\"Regla rechazada.\"}";
        return new HttpClientErrorException(estado, estado.getReasonPhrase(), json.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
    }
}

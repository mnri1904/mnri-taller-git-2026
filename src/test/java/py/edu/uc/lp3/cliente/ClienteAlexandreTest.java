package py.edu.uc.lp3.cliente;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class ClienteAlexandreTest {
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
    private final ClienteAlexandre cliente = new ClienteAlexandre(builder, "http://a3.test");
    private final String ruta = "http://a3.test/minecraft/simulaciones/explosion";

    @Test
    void enviaLaJugadaYLeeElResultadoDelArmero() {
        servidor.expect(requestTo(ruta)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"potencia":10,"radio":3,"posicionCreeper":"0,64,0","tiempoCarga":3}
                        """))
                .andRespond(withSuccess("""
                        {"potencia":10,"radio":3,"posicionCreeper":"0,64,0","vidaCreeper":0,
                         "aldeanos":[{"id":1,"nombre":"Pedro","tipo":"AldeanoArmero","vidaAntes":20,
                                      "vidaDespues":14,"danoRecibido":6,"distancia":0}]}
                        """, MediaType.APPLICATION_JSON));
        var resultado = cliente.simularExplosion(new ClienteAlexandre.Solicitud(10, 3, "0,64,0", 3));
        assertThat(resultado.aldeanos()).singleElement().satisfies(pedro -> {
            assertThat(pedro.vidaDespues()).isEqualTo(14);
            assertThat(pedro.danoRecibido()).isEqualTo(6);
        });
        servidor.verify();
    }

    @Test
    void conservaElEstadoYElCuerpoDelRechazoDeA3() {
        String cuerpo = """
                {"tipo":"REGLA_DE_JUEGO","mensaje":"La potencia de la explosión debe ser mayor a 0.","estado":400}
                """;
        servidor.expect(requestTo(ruta)).andRespond(withStatus(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON).body(cuerpo));
        var rechazo = assertThrows(HttpClientErrorException.BadRequest.class,
                () -> cliente.simularExplosion(new ClienteAlexandre.Solicitud(0, 3, "0,64,0", 3)));
        assertThat(rechazo.getResponseBodyAsString()).isEqualTo(cuerpo);
        servidor.verify();
    }
}

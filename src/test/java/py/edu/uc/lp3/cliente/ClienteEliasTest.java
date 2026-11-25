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

class ClienteEliasTest {
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();
    private final ClienteElias cliente = new ClienteElias(builder, "http://a2.test");

    @Test
    void enviaLosIdsEnSuLugarYLeeElDanioYLasVidas() {
        servidor.expect(requestTo("http://a2.test/api/hostiles/7/explosiones"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"zombieId":13,"distancia":1}
                        """))
                .andRespond(withSuccess("""
                        {"danio":9,
                         "creeper":{"id":7,"tipo":"CREEPER","nombre":"Chispa","vida":0,"rangoAggro":3,"vivo":false},
                         "zombie":{"id":13,"tipo":"ZOMBIE","nombre":"Sombra","vida":11,"rangoAggro":3,"vivo":true}}
                        """, MediaType.APPLICATION_JSON));
        var resultado = cliente.detonar(7, new ClienteElias.Solicitud(13, 1));
        assertThat(resultado.danio()).isEqualTo(9);
        assertThat(resultado.creeper().vida()).isZero();
        assertThat(resultado.zombie().vida()).isEqualTo(11);
        servidor.verify();
    }

    @Test
    void conservaElRechazoPorDistanciaSinRepetirElPedido() {
        String cuerpo = """
                {"codigo":"FUERA_DE_ALCANCE","mensaje":"El Zombie está a 4 bloques y el alcance es de 3"}
                """;
        servidor.expect(requestTo("http://a2.test/api/hostiles/7/explosiones"))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON).body(cuerpo));
        var error = assertThrows(HttpClientErrorException.class,
                () -> cliente.detonar(7, new ClienteElias.Solicitud(13, 4)));
        assertThat(error.getStatusCode().value()).isEqualTo(422);
        assertThat(error.getResponseBodyAsString()).isEqualTo(cuerpo);
        servidor.verify();
    }
}

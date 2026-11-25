package py.edu.uc.lp3.cliente;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ClienteAlexandre {
    private final RestClient http;

    public ClienteAlexandre(RestClient.Builder builder, @Value("${companeros.alexandre.url}") String url) {
        this.http = builder.baseUrl(url).build();
    }

    public Resultado simularExplosion(Solicitud solicitud) {
        Resultado resultado = http.post()
                .uri("/minecraft/simulaciones/explosion")
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(Resultado.class);
        if (resultado == null) {
            throw new RestClientException("A3 respondió sin el resultado de la explosión");
        }
        return resultado;
    }

    public record Solicitud(int potencia, int radio, String posicionCreeper, int tiempoCarga) {}

    public record Resultado(int potencia, int radio, String posicionCreeper, int vidaCreeper,
                            List<AldeanoAfectado> aldeanos) {}

    public record AldeanoAfectado(long id, String nombre, String tipo, int vidaAntes,
                                 int vidaDespues, int danoRecibido, double distancia) {}
}

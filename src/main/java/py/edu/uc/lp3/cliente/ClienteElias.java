package py.edu.uc.lp3.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ClienteElias {
    private final RestClient http;

    public ClienteElias(RestClient.Builder builder, @Value("${companeros.elias.url}") String url) {
        this.http = builder.baseUrl(url).build();
    }

    public Resultado detonar(long creeperId, Solicitud solicitud) {
        Resultado resultado = http.post()
                .uri("/api/hostiles/{id}/explosiones", creeperId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(Resultado.class);
        if (resultado == null) {
            throw new RestClientException("A2 respondió sin el resultado de la explosión");
        }
        return resultado;
    }

    public record Solicitud(long zombieId, int distancia) {}
    public record Resultado(int danio, Hostil creeper, Hostil zombie) {}
    public record Hostil(long id, String tipo, String nombre, int vida, int rangoAggro, boolean vivo) {}
}

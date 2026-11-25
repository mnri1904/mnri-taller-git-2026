package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import py.edu.uc.lp3.cliente.ClienteAlexandre;
import py.edu.uc.lp3.cliente.ClienteElias;
import py.edu.uc.lp3.domain.JugadaAldea;
import py.edu.uc.lp3.repository.JugadaAldeaRepository;

@Service
public class EscenarioAldeaService {
    private final ClienteAlexandre alexandre;
    private final ClienteElias elias;
    private final JugadaAldeaRepository jugadas;

    public EscenarioAldeaService(ClienteAlexandre alexandre, ClienteElias elias, JugadaAldeaRepository jugadas) {
        this.alexandre = alexandre;
        this.elias = elias;
        this.jugadas = jugadas;
    }

    public JugadaAldea ejecutar(ClienteAlexandre.Solicitud onda, long creeperEliasId, ClienteElias.Solicitud ataque) {
        ClienteAlexandre.Resultado resultadoOnda;
        try {
            resultadoOnda = alexandre.simularExplosion(onda);
        } catch (RestClientException e) {
            throw new FalloCompanero("A3 Alexandre", e);
        }

        ClienteElias.Resultado resultadoAtaque;
        try {
            resultadoAtaque = elias.detonar(creeperEliasId, ataque);
        } catch (RestClientException e) {
            throw new FalloCompanero("A2 Elías", e);
        }

        int conDano = (int) resultadoOnda.aldeanos().stream().filter(a -> a.danoRecibido() > 0).count();
        return jugadas.save(new JugadaAldea(creeperEliasId, ataque.zombieId(), resultadoAtaque.danio(),
                resultadoAtaque.zombie().vida(), conDano));
    }

    public static class FalloCompanero extends RuntimeException {
        private final String origen;

        FalloCompanero(String origen, RestClientException causa) {
            super("Falló la llamada a " + origen, causa);
            this.origen = origen;
        }

        public String getOrigen() { return origen; }
    }
}

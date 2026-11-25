package py.edu.uc.lp3.service;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.HttpClientErrorException;
import py.edu.uc.lp3.cliente.ClienteAlexandre;
import py.edu.uc.lp3.cliente.ClienteElias;
import py.edu.uc.lp3.domain.JugadaAldea;
import py.edu.uc.lp3.repository.JugadaAldeaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class EscenarioAldeaServiceTest {
    @Autowired EscenarioAldeaService escenario;
    @Autowired JugadaAldeaRepository jugadas;
    @MockitoBean ClienteAlexandre alexandre;
    @MockitoBean ClienteElias elias;

    private final ClienteAlexandre.Solicitud onda = new ClienteAlexandre.Solicitud(10, 3, "0,64,0", 3);
    private final ClienteElias.Solicitud ataque = new ClienteElias.Solicitud(2, 1);
    private final ClienteAlexandre.Resultado ondaAceptada = new ClienteAlexandre.Resultado(10, 3, "0,64,0", 0,
            List.of(new ClienteAlexandre.AldeanoAfectado(1, "Pedro", "Armero", 20, 14, 6, 0)));
    private Long idAnterior;

    @BeforeEach
    void prepararBase() {
        jugadas.deleteAll();
        idAnterior = jugadas.save(new JugadaAldea(30, 31, 3, 17, 0)).getId();
    }

    @Test
    void siA3RechazaNoSeLlamaAElíasYSeConservaLaJugadaAnterior() {
        var sinPotencia = new ClienteAlexandre.Solicitud(0, 3, "0,64,0", 3);
        when(alexandre.simularExplosion(sinPotencia)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        var error = assertThrows(EscenarioAldeaService.FalloCompanero.class,
                () -> escenario.ejecutar(sinPotencia, 1, ataque));
        assertThat(error.getOrigen()).isEqualTo("A3 Alexandre");
        verifyNoInteractions(elias);
        assertThat(jugadas.findAll()).extracting(JugadaAldea::getId).containsExactly(idAnterior);
    }

    @Test
    void siA2RechazaDespuesDeA3NoSeInsertaUnaJugada() {
        when(alexandre.simularExplosion(onda)).thenReturn(ondaAceptada);
        var fueraDeAlcance = new ClienteElias.Solicitud(2, 4);
        when(elias.detonar(1, fueraDeAlcance)).thenThrow(new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY));
        var error = assertThrows(EscenarioAldeaService.FalloCompanero.class,
                () -> escenario.ejecutar(onda, 1, fueraDeAlcance));
        assertThat(error.getOrigen()).isEqualTo("A2 Elías");
        var orden = inOrder(alexandre, elias);
        orden.verify(alexandre).simularExplosion(onda);
        orden.verify(elias).detonar(1, fueraDeAlcance);
        assertThat(jugadas.findAll()).extracting(JugadaAldea::getId).containsExactly(idAnterior);
    }

    @Test
    void ambosAceptanYElResultadoSePuedeVolverALeer() {
        when(alexandre.simularExplosion(onda)).thenReturn(ondaAceptada);
        when(elias.detonar(1, ataque)).thenReturn(new ClienteElias.Resultado(9,
                new ClienteElias.Hostil(1, "CREEPER", "Chispa", 0, 3, false),
                new ClienteElias.Hostil(2, "ZOMBIE", "Sombra", 11, 3, true)));
        Long id = escenario.ejecutar(onda, 1, ataque).getId();
        var guardada = jugadas.findById(id).orElseThrow();
        assertThat(guardada.getAldeanosConDano()).isEqualTo(1);
        assertThat(guardada.getVidaFinalZombie()).isEqualTo(11);
        assertThat(guardada.getDanoZombie()).isEqualTo(9);
        assertThat(jugadas.count()).isEqualTo(2);
    }
}

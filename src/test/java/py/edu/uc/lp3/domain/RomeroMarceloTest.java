package py.edu.uc.lp3.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.MnriTallerGit2026Application;
import py.edu.uc.lp3.service.EntidadService;
import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;
import py.edu.uc.lp3.domain.exception.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.exception.VidaInvalidaException;

@SpringBootTest(classes = MnriTallerGit2026Application.class)
@AutoConfigureMockMvc
class RomeroMarceloTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntidadService service;

    @Test
    void losConstructoresSimplesCreanMobsConVidaCompletaEnElOrigen() {
        for (Mob mob : new Mob[] {new Creeper(), new Zombie()}) {
            assertThat(mob.estado().vida()).isEqualTo(20);
            assertThat(mob.estado().posicionX()).isZero();
            assertThat(mob.estado().posicionZ()).isZero();
            assertThat(mob.estaViva()).isTrue();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 20})
    void losConstructoresSobrecargadosAceptanLosLimitesDeVida(int vida) {
        for (Mob mob : new Mob[] {new Creeper(vida, 100, -50), new Zombie(vida, 100, -50)}) {
            assertThat(mob.estado().vida()).isEqualTo(vida);
            assertThat(mob.estado().vidaMaxima()).isEqualTo(20);
            assertThat(mob.estado().posicionX()).isEqualTo(100);
            assertThat(mob.estado().posicionZ()).isEqualTo(-50);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 21})
    void elServicioRechazaVidaInicialInvalida(int vida) {
        for (String tipo : new String[]{"Creeper", "Zombie"}) {
            assertThatThrownBy(() -> service.crearMob(tipo, vida, 0, 0))
                    .isInstanceOf(VidaInvalidaException.class)
                    .hasMessage("La vida debe estar entre 1 y 20");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"creeper", "zombie"})
    void construyeConVidaYPosicionRecibidasPorUrl(String tipo) throws Exception {
        mockMvc.perform(get("/api/minecraft/mobs/" + tipo)
                .param("vida", "12")
                .param("x", "100")
                .param("z", "-50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entidad.vida").value(12))
                .andExpect(jsonPath("$.entidad.posicionX").value(100))
                .andExpect(jsonPath("$.entidad.posicionZ").value(-50));
    }

    @ParameterizedTest
    @ValueSource(strings = {"creeper", "zombie"})
    void informaPorHttpLaVidaRechazadaPorElServicio(String tipo) throws Exception {
        mockMvc.perform(get("/api/minecraft/mobs/" + tipo)
                .param("vida", "21")
                .param("x", "0")
                .param("z", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La vida debe estar entre 1 y 20"));
    }

    @Test
    void exponeElCreeperPorLaApiRest() throws Exception {
        mockMvc.perform(get("/api/minecraft/creeper"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entidad.tipo").value("Creeper"))
                .andExpect(jsonPath("$.entidad.vida").value(20))
                .andExpect(jsonPath("$.cargado").value(false));
    }

    @Test
    void exponeElIndiceDeLaApi() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proyecto").value("MNRI Taller Git 2026"))
                .andExpect(jsonPath("$.dominio").value("Minecraft"));
    }

    @Test
    void respondeConElComportamientoPolimorficoDelCreeper() throws Exception {
        mockMvc.perform(get("/api/minecraft/mobs/creeper")
                .param("x", "3")
                .param("z", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entidad.tipo").value("Creeper"))
                .andExpect(jsonPath("$.entidad.posicionX").value(3))
                .andExpect(jsonPath("$.entidad.posicionZ").value(2))
                .andExpect(jsonPath("$.comportamiento")
                        .value("El Creeper comenzó a cargar su explosión"));
    }

    @Test
    void respondeConElComportamientoPolimorficoDelZombie() throws Exception {
        mockMvc.perform(get("/api/minecraft/mobs/zombie")
                .param("x", "1")
                .param("z", "-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entidad.tipo").value("Zombie"))
                .andExpect(jsonPath("$.entidad.posicionX").value(1))
                .andExpect(jsonPath("$.entidad.posicionZ").value(-2))
                .andExpect(jsonPath("$.comportamiento")
                        .value("El Zombie atacó cuerpo a cuerpo al jugador"));
    }

    @Test
    void rechazaUnTipoDeMobDesconocido() throws Exception {
        mockMvc.perform(get("/api/minecraft/mobs/dragon")
                .param("x", "0")
                .param("z", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tipo de mob no soportado: dragon"));
    }

    @Test
    void recibirDanioNuncaDejaLaVidaNegativa() {
        Creeper creeper = new Creeper();

        service.recibirDanio(creeper, 30);

        assertThat(creeper.estado().vida()).isZero();
        assertThat(creeper.estaViva()).isFalse();
    }

    @Test
    void rechazaDanioQueNoSeaPositivo() {
        Creeper creeper = new Creeper();

        assertThatThrownBy(() -> service.recibirDanio(creeper, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El daño debe ser positivo");
        assertThat(creeper.estado().vida()).isEqualTo(20);
    }

    @Test
    void limitaElDesplazamientoDeUnMob() {
        Creeper creeper = new Creeper();

        service.mover(creeper, 3, 4);

        assertThat(creeper.estado().posicionX()).isEqualTo(3);
        assertThat(creeper.estado().posicionZ()).isEqualTo(4);
        assertThatThrownBy(() -> service.mover(creeper, 8, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un mob no puede desplazarse más de 8 bloques");
    }

    @Test
    void soloExplotaDespuesDeCargarYLuegoDesaparece() {
        Creeper creeper = new Creeper();

        assertThatThrownBy(() -> service.consumir(creeper))
                .isInstanceOf(ExplosionInvalidaException.class)
                .hasMessage("El Creeper debe cargar antes de explotar");

        service.atacar(creeper);
        String resultado = service.consumir(creeper);

        assertThat(resultado).isEqualTo("El Creeper explotó y desapareció");
        assertThat(creeper.estaViva()).isFalse();
        assertThat(creeper.estaCargado()).isFalse();
    }

    @Test
    void cancelaLaCargaCuandoElDanioLoHaceDesaparecer() {
        Creeper creeper = new Creeper();
        service.atacar(creeper);

        service.recibirDanio(creeper, 20);

        assertThat(creeper.estaViva()).isFalse();
        assertThat(creeper.estaCargado()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"8, 0, 12", "8, 3, 15", "8, 8, 20", "8, 10, 20", "30, 2, 0"})
    void laProteccionReduceElDanioSinCurarNiDejarVidaNegativa(int danio, int proteccion, int vidaEsperada) {
        for (Entidad entidad : new Entidad[] {new Creeper(), new Zombie()}) {
            service.recibirDanio(entidad, danio, proteccion);
            assertThat(entidad.estado().vida()).isEqualTo(vidaEsperada);
        }
    }

    @Test
    void rechazaArgumentosInvalidosDeLaSobrecargaSinCambiarElEstado() {
        Creeper creeper = new Creeper();
        assertThatThrownBy(() -> service.recibirDanio(creeper, 0, 3))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.recibirDanio(creeper, 8, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La protección no puede ser negativa");
        assertThat(creeper.estado().vida()).isEqualTo(20);
    }

    @Test
    void elDanioConProteccionMantieneLaReglaDeDesaparicion() {
        Creeper creeper = new Creeper();
        service.atacar(creeper);
        service.recibirDanio(creeper, 30, 5);
        assertThat(creeper.estaViva()).isFalse();
        assertThat(creeper.estaCargado()).isFalse();
        assertThatThrownBy(() -> service.recibirDanio(creeper, 1, 10))
                .isInstanceOf(EntidadDesaparecidaException.class);
    }

    @Test
    void permiteProbarAmbasFirmasDeDanioPorHttp() throws Exception {
        mockMvc.perform(post("/api/minecraft/creeper/reiniciar"))
                .andExpect(status().isOk());
        try {
            mockMvc.perform(post("/api/minecraft/creeper/danio")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"cantidad\":4}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.entidad.vida").value(16));
            mockMvc.perform(post("/api/minecraft/creeper/danio")
                    .param("proteccion", "3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"cantidad\":8}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.entidad.vida").value(11));
            mockMvc.perform(post("/api/minecraft/creeper/danio")
                    .param("proteccion", "-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"cantidad\":8}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("La protección no puede ser negativa"));
        } finally {
            mockMvc.perform(post("/api/minecraft/creeper/reiniciar"))
                    .andExpect(status().isOk());
        }
    }

}

package py.edu.uc.lp3.minecraft;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.mnritallergit2026.MnriTallerGit2026Application;

@SpringBootTest(classes = MnriTallerGit2026Application.class)
@AutoConfigureMockMvc
class RomeroMarceloTest {

    @Autowired
    private MockMvc mockMvc;

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
                        .value("El Creeper comenzo a cargar su explosion"));
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
                        .value("El Zombie ataco cuerpo a cuerpo al jugador"));
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

        creeper.recibirDanio(30);

        assertThat(creeper.estado().vida()).isZero();
        assertThat(creeper.estaViva()).isFalse();
    }

    @Test
    void rechazaDanioQueNoSeaPositivo() {
        Creeper creeper = new Creeper();

        assertThatThrownBy(() -> creeper.recibirDanio(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El danio debe ser positivo");
        assertThat(creeper.estado().vida()).isEqualTo(20);
    }

    @Test
    void aplicaLaReglaDeMovimientoHeredadaDelMob() {
        Creeper creeper = new Creeper();

        creeper.mover(3, 4);

        assertThat(creeper.estado().posicionX()).isEqualTo(3);
        assertThat(creeper.estado().posicionZ()).isEqualTo(4);
        assertThatThrownBy(() -> creeper.mover(8, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Un mob no puede desplazarse mas de 8 bloques");
    }

    @Test
    void soloExplotaDespuesDeCargarYLuegoDesaparece() {
        Creeper creeper = new Creeper();

        assertThatThrownBy(creeper::explotar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El Creeper debe cargar antes de explotar");

        creeper.atacar();
        String resultado = creeper.explotar();

        assertThat(resultado).isEqualTo("El Creeper exploto y desaparecio");
        assertThat(creeper.estaViva()).isFalse();
        assertThat(creeper.estaCargado()).isFalse();
    }

    @Test
    void cancelaLaCargaCuandoElDanioLoHaceDesaparecer() {
        Creeper creeper = new Creeper();
        creeper.atacar();

        creeper.recibirDanio(20);

        assertThat(creeper.estaViva()).isFalse();
        assertThat(creeper.estaCargado()).isFalse();
    }

}

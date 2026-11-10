package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UICapableTest {

    @Test
    void todasLasEntidadesExponenSuRecursoVisual() {
        UICapable[] dibujables = {new Creeper(), new Zombie(), new Cerdo()};
        String[] imagenes = {"minecraft:creeper", "minecraft:zombie", "minecraft:cerdo"};
        for (int i = 0; i < dibujables.length; i++) {
            assertEquals(imagenes[i], dibujables[i].imagen());
            assertEquals(new PosicionUI(0, 0), dibujables[i].posicion());
        }
    }

    @Test
    void laPosicionReflejaElMovimientoSinModificarLaCopiaAnterior() {
        Zombie zombie = new Zombie(20, 3, 2);
        UICapable dibujable = zombie;
        PosicionUI anterior = dibujable.posicion();
        zombie.mover(2, -1);
        assertEquals(new PosicionUI(5, 1), dibujable.posicion());
        assertEquals(new PosicionUI(3, 2), anterior);
    }

    @Test
    void elSonidoDelCreeperCambiaAlCargarYNoSeEmiteTrasExplotar() {
        Creeper creeper = new Creeper();
        UICapable dibujable = creeper;
        String reposo = dibujable.sonido();
        creeper.atacar();
        assertNotEquals(reposo, dibujable.sonido());
        creeper.explotar();
        assertThrows(IllegalStateException.class, dibujable::sonido);
    }

    @Test
    void elContratoVisualConservaLaReglaDeEntidadesVivas() {
        Entidad[] entidades = {new Zombie(), new Cerdo()};
        for (Entidad entidad : entidades) {
            UICapable dibujable = entidad;
            assertEquals(((Sonoro) entidad).emitirSonido(), dibujable.sonido());
            entidad.desaparecer();
            assertThrows(IllegalStateException.class, dibujable::sonido);
        }
    }
}

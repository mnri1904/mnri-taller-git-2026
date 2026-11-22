package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.service.EntidadService;
import py.edu.uc.lp3.repository.EntidadRepository;
import static org.mockito.Mockito.mock;
import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UICapableTest {
    private final EntidadService service = new EntidadService(mock(EntidadRepository.class));

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
        service.mover(zombie, 2, -1);
        assertEquals(new PosicionUI(5, 1), dibujable.posicion());
        assertEquals(new PosicionUI(3, 2), anterior);
    }

    @Test
    void elSonidoDelCreeperCambiaAlCargarYNoSeEmiteTrasExplotar() {
        Creeper creeper = new Creeper();
        UICapable dibujable = creeper;
        String reposo = dibujable.sonido();
        service.atacar(creeper);
        assertNotEquals(reposo, dibujable.sonido());
        service.consumir(creeper);
        assertThrows(EntidadDesaparecidaException.class, () -> service.sonido(creeper));
    }

    @Test
    void elContratoVisualConservaLaReglaDeEntidadesVivas() {
        Entidad[] entidades = {new Zombie(), new Cerdo()};
        for (Entidad entidad : entidades) {
            UICapable dibujable = entidad;
            assertEquals(((Sonoro) entidad).emitirSonido(), dibujable.sonido());
            service.recibirDanio(entidad, entidad.estado().vida());
            assertThrows(EntidadDesaparecidaException.class, () -> service.sonido(entidad));
        }
    }
}

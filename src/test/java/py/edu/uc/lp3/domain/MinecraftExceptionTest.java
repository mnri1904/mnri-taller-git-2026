package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;
import py.edu.uc.lp3.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.domain.exception.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.exception.MinecraftException;
import py.edu.uc.lp3.domain.exception.ReglaMinecraftException;
import py.edu.uc.lp3.domain.exception.VidaInvalidaException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MinecraftExceptionTest {

    @Test
    void lasReglasEspecificasCompartenUnaBaseDelDominio() {
        for (ReglaMinecraftException exception : new ReglaMinecraftException[] {
                new VidaInvalidaException("Vida invalida"), new EntidadDesaparecidaException(),
                new ExplosionInvalidaException("Explosion invalida")}) {
            assertThat(exception).isInstanceOf(MinecraftException.class);
        }
        assertThat(new EntidadNoEncontradaException(7L))
                .isInstanceOf(MinecraftException.class)
                .isNotInstanceOf(ReglaMinecraftException.class)
                .hasMessage("No existe la entidad con id 7");
    }

    @Test
    void vidaInvalidaNoModificaElEstadoAnterior() {
        Zombie zombie = new Zombie();
        var anterior = zombie.estado();
        assertThatThrownBy(() -> zombie.actualizarEstado(21, 5, 8))
                .isInstanceOf(VidaInvalidaException.class);
        assertThat(zombie.estado()).isEqualTo(anterior);
    }
}

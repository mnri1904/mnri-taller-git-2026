package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;
import py.edu.uc.lp3.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.domain.exception.ExplosionInvalidaException;
import py.edu.uc.lp3.domain.exception.MinecraftException;
import py.edu.uc.lp3.domain.exception.ReglaMinecraftException;
import py.edu.uc.lp3.domain.exception.VidaInvalidaException;

import static org.assertj.core.api.Assertions.assertThat;

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

}

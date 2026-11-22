package py.edu.uc.lp3.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import py.edu.uc.lp3.domain.Cerdo;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.domain.exception.EntidadDesaparecidaException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ExplosionTest {
    private final EntidadService service = new EntidadService(mock(EntidadRepository.class));

    @Test
    void danioDisminuyeConDistanciaIncluyendoElBordeDelRadio() {
        Creeper creeper = new Creeper();
        Zombie centro = new Zombie();
        Zombie aUnBloque = new Zombie(20, 1, 0);
        Zombie aDosBloques = new Zombie(20, 0, -2);
        Zombie enBorde = new Zombie(20, -3, 0);
        Zombie fuera = new Zombie(20, 3, 1);
        Cerdo cerdo = new Cerdo(10, 1, 1);
        Cerdo fragil = new Cerdo(2, 0, 0);
        Cerdo desaparecido = new Cerdo();
        service.recibirDanio(desaparecido, 10);
        Creeper otroCreeper = new Creeper();

        var resultado = service.detonarSobre(creeper, List.of(creeper, centro, aUnBloque, aDosBloques,
                enBorde, fuera, cerdo, fragil, desaparecido, otroCreeper));

        assertThat(resultado.afectados()).hasSize(6);
        assertThat(centro.estado().vida()).isEqualTo(8);
        assertThat(aUnBloque.estado().vida()).isEqualTo(11);
        assertThat(aDosBloques.estado().vida()).isEqualTo(14);
        assertThat(enBorde.estado().vida()).isEqualTo(17);
        assertThat(fuera.estado().vida()).isEqualTo(20);
        assertThat(cerdo.estado().vida()).isEqualTo(1);
        assertThat(fragil.estado().vida()).isZero();
        assertThat(resultado.afectados().getLast().danioAplicado()).isEqualTo(2);
        assertThat(desaparecido.estado().vida()).isZero();
        assertThat(otroCreeper.estaViva()).isTrue();
        assertThat(creeper.estaViva()).isFalse();
        assertThat(creeper.estaCargado()).isFalse();
        assertThat(resultado.creeper().vida()).isZero();
    }

    @Test
    void noPuedeVolverAExplotarNiDaniarOtroObjetivo() {
        Creeper creeper = new Creeper();
        service.detonarSobre(creeper, List.of());
        Zombie zombie = new Zombie();

        assertThatThrownBy(() -> service.detonarSobre(creeper, List.of(zombie)))
                .isInstanceOf(EntidadDesaparecidaException.class);
        assertThat(zombie.estado().vida()).isEqualTo(20);
    }

    @Test
    void posicionesExtremasNoDesbordanLaDistancia() {
        Creeper creeper = new Creeper(20, Integer.MIN_VALUE, Integer.MIN_VALUE);
        Zombie lejano = new Zombie(20, Integer.MAX_VALUE, Integer.MAX_VALUE);

        assertThat(service.detonarSobre(creeper, List.of(lejano)).afectados()).isEmpty();
        assertThat(lejano.estado().vida()).isEqualTo(20);
    }
}

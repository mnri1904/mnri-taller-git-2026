package py.edu.uc.lp3.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExplosionTest {

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
        desaparecido.desaparecer();
        Creeper otroCreeper = new Creeper();

        var resultado = creeper.detonarSobre(List.of(creeper, centro, aUnBloque, aDosBloques,
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
        creeper.detonarSobre(List.of());
        Zombie zombie = new Zombie();

        assertThatThrownBy(() -> creeper.detonarSobre(List.of(zombie)))
                .isInstanceOf(IllegalStateException.class);
        assertThat(zombie.estado().vida()).isEqualTo(20);
    }

    @Test
    void posicionesExtremasNoDesbordanLaDistancia() {
        Creeper creeper = new Creeper(20, Integer.MIN_VALUE, Integer.MIN_VALUE);
        Zombie lejano = new Zombie(20, Integer.MAX_VALUE, Integer.MAX_VALUE);

        assertThat(creeper.detonarSobre(List.of(lejano)).afectados()).isEmpty();
        assertThat(lejano.estado().vida()).isEqualTo(20);
    }
}

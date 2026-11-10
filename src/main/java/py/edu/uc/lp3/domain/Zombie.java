package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Zombie")
public class Zombie extends Mob implements Sonoro {

    private static final int VIDA_MAXIMA = 20;

    public Zombie() {
        this(VIDA_MAXIMA, 0, 0);
    }

    public Zombie(int vidaInicial, int posicionX, int posicionZ) {
        super(VIDA_MAXIMA, vidaInicial, posicionX, posicionZ);
    }

    @Override
    public String tipo() {
        return "Zombie";
    }

    @Override
    public String atacar() {
        exigirActiva();
        return "El Zombie ataco cuerpo a cuerpo al jugador";
    }

    @Override
    public String emitirSonido() {
        exigirActiva();
        return "Grrr: el zombie gime buscando un objetivo.";
    }

    @Override
    public String sonido() {
        return emitirSonido();
    }

    @Override
    public double velocidad() {
        return 0.23;
    }
}

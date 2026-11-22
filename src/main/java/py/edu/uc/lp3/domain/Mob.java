package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

@Entity
public abstract class Mob extends Entidad {

    protected Mob() {
    }

    protected Mob(int vidaMaxima) {
        super(vidaMaxima);
    }

    protected Mob(int vidaMaxima, int vidaInicial, int posicionX, int posicionZ) {
        super(vidaMaxima, vidaInicial, posicionX, posicionZ);
    }

    public abstract String atacar();
}

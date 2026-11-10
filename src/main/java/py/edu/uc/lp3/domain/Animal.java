package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

@Entity
public abstract class Animal extends Entidad {

    protected Animal() {
    }

    protected Animal(int vidaMaxima) {
        super(vidaMaxima);
    }

    protected Animal(int vidaMaxima, int vidaInicial, int posicionX, int posicionZ) {
        super(vidaMaxima, vidaInicial, posicionX, posicionZ);
    }
}

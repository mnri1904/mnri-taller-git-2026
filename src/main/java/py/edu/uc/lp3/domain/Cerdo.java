package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Cerdo")
public class Cerdo extends Animal implements Sonoro {

    private static final int VIDA_MAXIMA = 10;

    public Cerdo() {
        this(VIDA_MAXIMA, 0, 0);
    }

    public Cerdo(int vidaInicial, int posicionX, int posicionZ) {
        super(VIDA_MAXIMA, vidaInicial, posicionX, posicionZ);
    }

    @Override
    public String tipo() {
        return "Cerdo";
    }

    @Override
    public String emitirSonido() {
        exigirActiva();
        return "Oink, oink: el cerdo gruñe tranquilamente.";
    }

    @Override
    public String sonido() {
        return emitirSonido();
    }

    @Override
    public double velocidad() {
        return 0.25;
    }
}

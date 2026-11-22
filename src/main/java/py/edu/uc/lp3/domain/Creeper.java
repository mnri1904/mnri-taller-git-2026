package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Creeper")
public class Creeper extends Mob {

    public static final int VIDA_MAXIMA = 20;

    private boolean cargado;

    public Creeper() {
        this(VIDA_MAXIMA, 0, 0);
    }

    public Creeper(int vidaInicial, int posicionX, int posicionZ) {
        super(VIDA_MAXIMA, vidaInicial, posicionX, posicionZ);
    }

    @Override
    public String tipo() {
        return "Creeper";
    }

    @Override
    public String atacar() {
        return "El Creeper comenzó a cargar su explosión";
    }

    public void cargar() {
        cargado = true;
    }

    public boolean estaCargado() {
        return cargado;
    }

    public void descargar() {
        cargado = false;
    }

    @Override
    public String sonido() {
        return cargado ? "Ssssss: el Creeper está cargando su explosión." : "El Creeper está en silencio.";
    }

    @Override
    public double velocidad() {
        return 0.25;
    }
}

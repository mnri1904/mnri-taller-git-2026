package py.edu.uc.lp3.domain;

public final class Zombie extends Mob {

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
}

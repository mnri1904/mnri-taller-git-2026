package py.edu.uc.lp3.minecraft;

public final class Zombie extends Mob {

    private static final int VIDA_MAXIMA = 20;

    public Zombie() {
        super(VIDA_MAXIMA);
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

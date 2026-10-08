package py.edu.uc.lp3.domain;

public abstract class Mob extends Entidad {

    private static final int DESPLAZAMIENTO_MAXIMO = 8;

    protected Mob(int vidaMaxima) {
        super(vidaMaxima);
    }

    protected Mob(int vidaMaxima, int vidaInicial, int posicionX, int posicionZ) {
        super(vidaMaxima, vidaInicial, posicionX, posicionZ);
    }

    @Override
    public void mover(int desplazamientoX, int desplazamientoZ) {
        long distancia = Math.abs((long) desplazamientoX) + Math.abs((long) desplazamientoZ);
        if (distancia > DESPLAZAMIENTO_MAXIMO) {
            throw new IllegalArgumentException("Un mob no puede desplazarse mas de 8 bloques");
        }
        super.mover(desplazamientoX, desplazamientoZ);
    }

    public abstract String atacar();
}

package py.edu.uc.lp3.minecraft;

public abstract class Entidad {

    private final int vidaMaxima;
    private int vida;
    private int posicionX;
    private int posicionZ;

    protected Entidad(int vidaMaxima) {
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida maxima debe ser positiva");
        }
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
    }

    public abstract String tipo();

    public void mover(int desplazamientoX, int desplazamientoZ) {
        exigirActiva();
        if (desplazamientoX == 0 && desplazamientoZ == 0) {
            throw new IllegalArgumentException("El desplazamiento no puede ser cero");
        }

        try {
            int nuevaPosicionX = Math.addExact(posicionX, desplazamientoX);
            int nuevaPosicionZ = Math.addExact(posicionZ, desplazamientoZ);
            posicionX = nuevaPosicionX;
            posicionZ = nuevaPosicionZ;
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("La posicion excede el mundo permitido", exception);
        }
    }

    public final void recibirDanio(int cantidad) {
        exigirActiva();
        if (cantidad <= 0) {
            throw new IllegalArgumentException("El danio debe ser positivo");
        }
        vida = Math.max(0, vida - cantidad);
        if (!estaViva()) {
            alDesaparecer();
        }
    }

    public final void desaparecer() {
        vida = 0;
        alDesaparecer();
    }

    public final boolean estaViva() {
        return vida > 0;
    }

    public final EstadoEntidad estado() {
        return new EstadoEntidad(tipo(), vida, vidaMaxima, posicionX, posicionZ, estaViva());
    }

    protected final void exigirActiva() {
        if (!estaViva()) {
            throw new IllegalStateException("La entidad ya desaparecio");
        }
    }

    protected void alDesaparecer() {
    }
}

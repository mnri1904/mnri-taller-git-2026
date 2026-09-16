package py.edu.uc.lp3.minecraft;

public final class Creeper extends Mob {

    private static final int VIDA_MAXIMA = 20;

    private boolean cargado;

    public Creeper() {
        super(VIDA_MAXIMA);
    }

    @Override
    public String tipo() {
        return "Creeper";
    }

    @Override
    public String atacar() {
        exigirActiva();
        cargado = true;
        return "El Creeper comenzo a cargar su explosion";
    }

    public String explotar() {
        exigirActiva();
        if (!cargado) {
            throw new IllegalStateException("El Creeper debe cargar antes de explotar");
        }
        desaparecer();
        return "El Creeper exploto y desaparecio";
    }

    public boolean estaCargado() {
        return cargado;
    }

    @Override
    protected void alDesaparecer() {
        cargado = false;
    }
}

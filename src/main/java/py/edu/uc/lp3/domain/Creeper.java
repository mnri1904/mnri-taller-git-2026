package py.edu.uc.lp3.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Creeper")
public class Creeper extends Mob {

    private static final int VIDA_MAXIMA = 20;
    private static final int RADIO_EXPLOSION = 3;

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

    public ResultadoExplosion detonarSobre(List<? extends Entidad> entidades) {
        exigirActiva();
        atacar();
        var afectados = new ArrayList<ResultadoExplosion.Impacto>();
        for (Entidad objetivo : entidades) {
            if (!(objetivo instanceof Zombie || objetivo instanceof Cerdo) || !objetivo.estaViva()) {
                continue;
            }
            double distancia = Math.hypot(
                    (double) objetivo.posicion().x() - posicion().x(),
                    (double) objetivo.posicion().z() - posicion().z());
            if (distancia > RADIO_EXPLOSION) {
                continue;
            }
            int vidaAnterior = objetivo.estado().vida();
            int danio = 12 - 3 * (int) Math.floor(distancia);
            objetivo.recibirDanio(danio);
            afectados.add(new ResultadoExplosion.Impacto(objetivo.getId(), objetivo.tipo(), distancia,
                    vidaAnterior - objetivo.estado().vida(), vidaAnterior,
                    objetivo.estado().vida(), objetivo.estaViva()));
        }
        explotar();
        return new ResultadoExplosion(getId(), estado(), RADIO_EXPLOSION, afectados);
    }

    @Override
    public String sonido() {
        exigirActiva();
        return cargado ? "Ssssss: el Creeper esta cargando su explosion." : "El Creeper esta en silencio.";
    }

    @Override
    public double velocidad() {
        return 0.25;
    }

    @Override
    protected void alDesaparecer() {
        cargado = false;
    }
}

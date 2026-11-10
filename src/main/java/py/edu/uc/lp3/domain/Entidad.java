package py.edu.uc.lp3.domain;

import java.util.Locale;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "entidades")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class Entidad implements UICapable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int vidaMaxima;
    private int vida;
    private int posicionX;
    private int posicionZ;

    protected Entidad() {
        // JPA reemplaza este estado legal al hidratar los campos persistidos.
        this(1);
    }

    protected Entidad(int vidaMaxima) {
        this(vidaMaxima, vidaMaxima, 0, 0);
    }

    protected Entidad(int vidaMaxima, int vidaInicial, int posicionX, int posicionZ) {
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida maxima debe ser positiva");
        }
        if (vidaInicial <= 0 || vidaInicial > vidaMaxima) {
            throw new IllegalArgumentException("La vida inicial debe estar entre 1 y " + vidaMaxima);
        }
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaInicial;
        this.posicionX = posicionX;
        this.posicionZ = posicionZ;
    }

    public abstract String tipo();

    public Long getId() {
        return id;
    }

    public final void actualizarEstado(int vida, int posicionX, int posicionZ) {
        if (vida <= 0 || vida > vidaMaxima) {
            throw new IllegalArgumentException("La vida debe estar entre 1 y " + vidaMaxima);
        }
        this.vida = vida;
        this.posicionX = posicionX;
        this.posicionZ = posicionZ;
    }

    @Override
    public String imagen() {
        return "minecraft:" + tipo().toLowerCase(Locale.ROOT);
    }

    @Override
    public final PosicionUI posicion() {
        return new PosicionUI(posicionX, posicionZ);
    }

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

    public final void recibirDanio(int cantidad, int proteccion) {
        exigirActiva();
        if (cantidad <= 0) {
            throw new IllegalArgumentException("El danio debe ser positivo");
        }
        if (proteccion < 0) {
            throw new IllegalArgumentException("La proteccion no puede ser negativa");
        }
        int danioEfectivo = Math.max(0, cantidad - proteccion);
        if (danioEfectivo > 0) {
            recibirDanio(danioEfectivo);
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

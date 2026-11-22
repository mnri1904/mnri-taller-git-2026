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

    public final boolean estaViva() {
        return vida > 0;
    }

    public final EstadoEntidad estado() {
        return new EstadoEntidad(tipo(), vida, vidaMaxima, posicionX, posicionZ, estaViva());
    }
}

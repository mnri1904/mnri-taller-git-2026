package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jugadas_aldea")
public class JugadaAldea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long creeperEliasId;
    private long zombieEliasId;
    private int danoZombie;
    private int vidaFinalZombie;
    private int aldeanosConDano;

    protected JugadaAldea() {}

    public JugadaAldea(long creeperEliasId, long zombieEliasId, int danoZombie,
                       int vidaFinalZombie, int aldeanosConDano) {
        this.creeperEliasId = creeperEliasId;
        this.zombieEliasId = zombieEliasId;
        this.danoZombie = danoZombie;
        this.vidaFinalZombie = vidaFinalZombie;
        this.aldeanosConDano = aldeanosConDano;
    }

    public Long getId() { return id; }
    public long getCreeperEliasId() { return creeperEliasId; }
    public long getZombieEliasId() { return zombieEliasId; }
    public int getDanoZombie() { return danoZombie; }
    public int getVidaFinalZombie() { return vidaFinalZombie; }
    public int getAldeanosConDano() { return aldeanosConDano; }
}

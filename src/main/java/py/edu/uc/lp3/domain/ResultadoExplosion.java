package py.edu.uc.lp3.domain;

import java.util.List;

public record ResultadoExplosion(Long creeperId, EstadoEntidad creeper, int radio,
        List<Impacto> afectados) {

    public ResultadoExplosion {
        afectados = List.copyOf(afectados);
    }

    public record Impacto(Long id, String tipo, double distancia, int danioAplicado,
            int vidaAnterior, int vidaActual, boolean viva) {
    }
}

package py.edu.uc.lp3.domain;

public record EstadoEntidad(
        String tipo,
        int vida,
        int vidaMaxima,
        int posicionX,
        int posicionZ,
        boolean viva) {
}

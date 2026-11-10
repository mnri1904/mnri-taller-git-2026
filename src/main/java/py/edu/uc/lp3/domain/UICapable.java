package py.edu.uc.lp3.domain;

public interface UICapable {

    // Identificador del recurso visual; la UI lo resuelve a una imagen.
    String imagen();

    PosicionUI posicion();

    String sonido();

    // Atributo de velocidad de movimiento del modelo de Minecraft.
    double velocidad();
}

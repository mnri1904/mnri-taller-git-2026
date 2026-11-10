package py.edu.uc.lp3.domain.exception;

public abstract class MinecraftException extends RuntimeException {

    protected MinecraftException(String mensaje) {
        super(mensaje);
    }
}

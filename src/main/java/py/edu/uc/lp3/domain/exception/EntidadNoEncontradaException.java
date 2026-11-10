package py.edu.uc.lp3.domain.exception;

public class EntidadNoEncontradaException extends MinecraftException {

    public EntidadNoEncontradaException(Long id) {
        super("No existe la entidad con id " + id);
    }
}

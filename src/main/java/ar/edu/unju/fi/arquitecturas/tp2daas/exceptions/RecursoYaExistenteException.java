package ar.edu.unju.fi.arquitecturas.tp2daas.exceptions;

public class RecursoYaExistenteException extends RuntimeException {
    public RecursoYaExistenteException(String message) {
        super(message);
    }
}

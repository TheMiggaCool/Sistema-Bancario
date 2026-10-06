package ar.edu.unju.fi.arquitecturas.tp2daas.exceptions;

public class OperacionNoPermitidaException extends RuntimeException {
    public OperacionNoPermitidaException(String message) {
        super(message);
    }
}

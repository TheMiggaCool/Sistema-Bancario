package ar.edu.unju.fi.arquitecturas.tp2daas.exceptions;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}

package ar.edu.unju.fi.arquitecturas.tp2daas.exceptions;

public class LimiteDiarioExcedidoException extends RuntimeException {
    public LimiteDiarioExcedidoException(String mensaje) {
        super(mensaje);
    }
}
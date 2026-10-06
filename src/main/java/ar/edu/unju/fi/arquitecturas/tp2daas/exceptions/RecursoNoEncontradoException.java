package ar.edu.unju.fi.arquitecturas.tp2daas.exceptions;

public class RecursoNoEncontradoException extends RuntimeException{
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

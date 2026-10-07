package ar.edu.unju.fi.arquitecturas.tp2daas.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ClienteCreadoEvent extends ApplicationEvent {
    private final String email;
    private final String nombre;
    private final String token;

    public ClienteCreadoEvent(Object source, String email, String nombre, String token) {
        super(source);
        this.email = email;
        this.nombre = nombre;
        this.token = token;
    }
}
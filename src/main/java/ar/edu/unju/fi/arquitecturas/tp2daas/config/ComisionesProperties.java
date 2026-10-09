package ar.edu.unju.fi.arquitecturas.tp2daas.config;

import ar.edu.unju.fi.arquitecturas.tp2daas.exceptions.OperacionNoPermitidaException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.comisiones")
public record ComisionesProperties(double cuentaCorriente, double cajaAhorro) {
    public ComisionesProperties {
        if (cuentaCorriente < 0 || cajaAhorro < 0) {
            throw new OperacionNoPermitidaException("Las comisiones configuradas no pueden ser negativas.");
        }
    }
}

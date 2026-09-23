package ar.edu.unju.fi.arquitecturas.tp2daas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name = "caja_de_ahorro")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class CajaDeAhorro extends CuentasFinancieras{
    private double tasaInteres;
    private int extraccionesSinCosto;

}

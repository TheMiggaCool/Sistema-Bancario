package ar.edu.unju.fi.arquitecturas.tp2daas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Table(name = "cuentas_corrientes")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class CuentaCorriente extends CuentaBancaria {
    private double margenAutorizado;
    private double costoMantenimiento;
}

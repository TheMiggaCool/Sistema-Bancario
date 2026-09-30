package ar.edu.unju.fi.arquitecturas.tp2daas.dto.response;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoCuenta;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de salida con la información de una cuenta bancaria.
 * <p>
 * Los campos específicos de cada tipo de cuenta se informan solo cuando corresponden;
 * en caso contrario permanecen en {@code null}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentasFinancierasResponseDTO {

    /**
     * Identificador único de la cuenta.
     */
    private Long id;

    /**
     * Tipo de cuenta (caja de ahorro o cuenta corriente).
     */
    private TipoCuenta tipoCuenta;

    /**
     * Clave Bancaria Uniforme de la cuenta.
     */
    private String cbu;

    /**
     * Alias de la cuenta.
     */
    private String alias;

    /**
     * Saldo actual de la cuenta.
     */
    private double saldo;

    /**
     * Estado operativo de la cuenta.
     */
    private EstadoCuenta estado;

    /**
     * Identificador del cliente titular.
     */
    private Long clienteId;

    /**
     * Fecha y hora de creación de la cuenta.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación de la cuenta.
     */
    private LocalDateTime fechaUltimaModificacion;

    /**
     * Tasa de interés (solo cajas de ahorro).
     */
    private Double tasaInteres;

    /**
     * Extracciones sin costo (solo cajas de ahorro).
     */
    private Integer extraccionesSinCosto;

    /**
     * Margen de descubierto autorizado (solo cuentas corrientes).
     */
    private Double margenAutorizado;

    /**
     * Costo de mantenimiento (solo cuentas corrientes).
     */
    private Double costoMantenimiento;
}

package ar.edu.unju.fi.arquitecturas.tp2daas.dto.request;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoCuenta;
import lombok.*;

/**
 * DTO de entrada para la creación y actualización de cuentas bancarias.
 * <p>
 * En la creación se utilizan todos los campos. En la actualización solamente se
 * consideran el alias y los parámetros propios de cada tipo de cuenta; el CBU,
 * el cliente titular y el saldo no son modificables por esta vía.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentasFinancierasRequestDTO {

    /**
     * Identificador del cliente titular de la cuenta (solo en la creación).
     */
    private Long clienteId;

    /**
     * Tipo de cuenta a crear (solo en la creación).
     */
    private TipoCuenta tipoCuenta;

    /**
     * Clave Bancaria Uniforme de 22 dígitos (solo en la creación).
     */
    private String cbu;

    /**
     * Alias único de la cuenta.
     */
    private String alias;

    /**
     * Saldo con el que se abre la cuenta (solo en la creación).
     */
    private double saldoInicial;

    /**
     * Tasa de interés. Aplica únicamente a cajas de ahorro.
     */
    private Double tasaInteres;

    /**
     * Cantidad de extracciones sin costo. Aplica únicamente a cajas de ahorro.
     */
    private Integer extraccionesSinCosto;

    /**
     * Margen de descubierto autorizado. Aplica únicamente a cuentas corrientes.
     */
    private Double margenAutorizado;

    /**
     * Costo de mantenimiento de la cuenta. Aplica únicamente a cuentas corrientes.
     */
    private Double costoMantenimiento;
}

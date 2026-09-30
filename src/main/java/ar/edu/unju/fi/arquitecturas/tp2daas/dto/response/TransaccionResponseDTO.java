package ar.edu.unju.fi.arquitecturas.tp2daas.dto.response;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import lombok.*;

import java.util.Date;

/**
 * DTO de salida con la información de una transacción bancaria.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionResponseDTO {

    /**
     * Identificador único de la transacción.
     */
    private Long id;

    /**
     * Fecha y hora en la que se registró la transacción.
     */
    private Date fecha;

    /**
     * Monto de la transacción.
     */
    private double monto;

    /**
     * Tipo de operación realizada.
     */
    private TipoTransaccion tipo;

    /**
     * Estado de la transacción.
     */
    private EstadoTransaccion estado;

    /**
     * Identificador de la cuenta a la que pertenece la transacción.
     */
    private Long cuentaBancariaId;
}

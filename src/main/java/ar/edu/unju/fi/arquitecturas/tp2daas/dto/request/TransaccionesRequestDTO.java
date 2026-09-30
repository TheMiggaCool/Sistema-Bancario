package ar.edu.unju.fi.arquitecturas.tp2daas.dto.request;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import lombok.*;

/**
 * DTO de entrada para el registro de una transacción bancaria.
 * <p>
 * El estado y la fecha de la transacción no se reciben: los define el sistema al registrarla.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionesRequestDTO {

    /**
     * Identificador de la cuenta sobre la que se opera (cuenta origen en una transferencia).
     */
    private Long cuentaFinancieraId;

    /**
     * Tipo de operación. Las transferencias se solicitan como
     * {@link TipoTransaccion#TRANSFERENCIA_ENVIADA}; la contraparte
     * {@link TipoTransaccion#TRANSFERENCIA_RECIBIDA} la genera el sistema.
     */
    private TipoTransaccion tipo;

    /**
     * Monto de la operación. Debe ser mayor a cero.
     */
    private double monto;

    /**
     * Identificador de la cuenta destino. Obligatorio únicamente para transferencias.
     */
    private Long cuentaDestinoId;
}

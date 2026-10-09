package ar.edu.unju.fi.arquitecturas.tp2daas.service.interfaces;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.ResumenLiquidacionDTO;

/**
 * Lógica de negocio de la liquidación mensual de comisiones de mantenimiento.
 */
public interface ComisionService {
    /**
     * Recorre las cuentas ACTIVAS y registra un {@code DEBITO_COMISION} por el importe fijo
     * configurado según el tipo de cuenta. Cada cuenta se procesa en su propia transacción.
     *
     * @return Resumen de la ejecución.
     */
    ResumenLiquidacionDTO liquidarComisionesMensuales();
}

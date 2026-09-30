package ar.edu.unju.fi.arquitecturas.tp2daas.service;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.TransaccionRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.TransaccionResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * Servicio con la lógica de negocio para el registro y la consulta de transacciones bancarias.
 * Las transacciones constituyen un registro inmutable, por lo que no se ofrecen operaciones
 * de modificación ni eliminación.
 */
public interface TransaccionService {

    /**
     * Registra una transacción y actualiza el saldo de la o las cuentas involucradas dentro
     * de una única transacción atómica. Si es una transferencia, registra origen y destino.
     *
     * @param request Datos de la transacción a procesar.
     * @return DTO con la transacción registrada sobre la cuenta principal/origen.
     */
    TransaccionResponseDTO registrarTransaccion(TransaccionRequestDTO request);

    /**
     * Obtiene una transacción por su identificador único.
     *
     * @param id Identificador de la transacción.
     * @return DTO con la transacción encontrada.
     */
    TransaccionResponseDTO obtenerPorId(Long id);

    /**
     * Lista de forma paginada todas las transacciones registradas en el sistema.
     *
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página de transacciones.
     */
    Page<TransaccionResponseDTO> listarTodas(Pageable pageable);

    /**
     * Lista de forma paginada el historial de transacciones de una cuenta específica.
     *
     * @param cuentaId Identificador de la cuenta bancaria.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con los movimientos de la cuenta.
     */
    Page<TransaccionResponseDTO> listarPorCuenta(Long cuentaId, Pageable pageable);

    /**
     * Lista transacciones de una cuenta filtradas por su tipo de operación.
     *
     * @param cuentaId Identificador de la cuenta bancaria.
     * @param tipo     Tipo de transacción a filtrar.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con las transacciones que coinciden con el tipo indicado.
     */
    Page<TransaccionResponseDTO> listarPorCuentaYTipo(Long cuentaId, TipoTransaccion tipo, Pageable pageable);

    /**
     * Lista transacciones de una cuenta filtradas por su estado.
     *
     * @param cuentaId Identificador de la cuenta bancaria.
     * @param estado   Estado a filtrar.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con las transacciones del estado indicado.
     */
    Page<TransaccionResponseDTO> listarPorCuentaYEstado(Long cuentaId, EstadoTransaccion estado, Pageable pageable);

    /**
     * Recupera el extracto de movimientos de una cuenta comprendido dentro de un rango temporal.
     *
     * @param cuentaId Identificador de la cuenta bancaria.
     * @param desde    Fecha de inicio del rango.
     * @param hasta    Fecha de fin del rango.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con las transacciones del período.
     */
    Page<TransaccionResponseDTO> listarPorCuentaEntreFechas(Long cuentaId, Date desde, Date hasta, Pageable pageable);
}
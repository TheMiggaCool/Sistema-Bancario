package ar.edu.unju.fi.arquitecturas.tp2daas.service;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.TransaccionesRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.TransaccionesResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * Servicio con la lógica de negocio para el registro y la consulta de transacciones bancarias.
 * <p>
 * Las transacciones constituyen un registro histórico, por lo que no se ofrecen operaciones
 * de modificación ni de eliminación. Todos los listados son paginados mediante {@link Page}
 * y el orden de los resultados lo define el {@link Pageable} recibido.
 */
public interface TransaccionesService {

    /**
     * Registra una transacción y actualiza el saldo de la o las cuentas involucradas dentro
     * de una única transacción de base de datos. Si la operación es una transferencia,
     * se registran ambos movimientos (enviada y recibida).
     *
     * @param request Datos de la transacción a registrar.
     * @return DTO con la transacción registrada sobre la cuenta indicada como origen.
     */
    TransaccionesResponseDTO registrarTransaccion(TransaccionesRequestDTO request);

    /**
     * Obtiene una transacción por su identificador.
     *
     * @param id Identificador único de la transacción.
     * @return DTO con la transacción encontrada.
     */
    TransaccionesResponseDTO obtenerPorId(Long id);

    /**
     * Lista de forma paginada todas las transacciones del sistema.
     *
     * @param pageable Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} de DTO con la página de transacciones solicitada.
     */
    Page<TransaccionesResponseDTO> listarTodas(Pageable pageable);

    /**
     * Lista de forma paginada el historial de transacciones de una cuenta.
     *
     * @param cuentaId Identificador único de la cuenta.
     * @param pageable Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} de DTO con la página de transacciones de la cuenta.
     */
    Page<TransaccionesResponseDTO> listarPorCuenta(Long cuentaId, Pageable pageable);

    /**
     * Lista de forma paginada las transacciones de una cuenta filtradas por tipo.
     *
     * @param cuentaId Identificador único de la cuenta.
     * @param tipo     Tipo de transacción a filtrar.
     * @param pageable Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} de DTO con las transacciones del tipo indicado.
     */
    Page<TransaccionesResponseDTO> listarPorCuentaYTipo(Long cuentaId, TipoTransaccion tipo, Pageable pageable);

    /**
     * Lista de forma paginada las transacciones de una cuenta filtradas por estado.
     *
     * @param cuentaId Identificador único de la cuenta.
     * @param estado   Estado de transacción a filtrar.
     * @param pageable Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} de DTO con las transacciones del estado indicado.
     */
    Page<TransaccionesResponseDTO> listarPorCuentaYEstado(Long cuentaId, EstadoTransaccion estado,
                                                          Pageable pageable);

    /**
     * Lista de forma paginada las transacciones de una cuenta dentro de un rango de fechas.
     *
     * @param cuentaId Identificador único de la cuenta.
     * @param desde    Fecha y hora de inicio del rango (inclusive).
     * @param hasta    Fecha y hora de fin del rango (inclusive).
     * @param pageable Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} de DTO con las transacciones comprendidas en el rango.
     */
    Page<TransaccionesResponseDTO> listarPorCuentaEntreFechas(Long cuentaId, Date desde, Date hasta,
                                                              Pageable pageable);
}

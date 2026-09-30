package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Transacciones;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link Transacciones}.
 * <p>
 * Los listados se resuelven de forma paginada mediante {@link Page} para no cargar
 * el historial completo de transacciones en memoria. El orden de los resultados
 * se define a través del {@link Pageable} recibido.
 */
@Repository
public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {

    /**
     * Recupera de forma paginada el historial de transacciones de una cuenta.
     *
     * @param cuentaFinancieraId Identificador único de la cuenta.
     * @param pageable           Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} con las transacciones de la cuenta correspondientes a la página solicitada.
     */
    Page<Transacciones> findByCuentaFinancieraId(Long cuentaFinancieraId, Pageable pageable);

    /**
     * Recupera de forma paginada las transacciones de una cuenta filtradas por tipo de operación.
     *
     * @param cuentaFinancieraId Identificador único de la cuenta.
     * @param tipo               Tipo de transacción a filtrar.
     * @param pageable           Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} con las transacciones que coinciden con el tipo indicado.
     */
    Page<Transacciones> findByCuentaFinancieraIdAndTipo(Long cuentaFinancieraId, TipoTransaccion tipo,
                                                        Pageable pageable);

    /**
     * Recupera de forma paginada las transacciones de una cuenta filtradas por estado.
     *
     * @param cuentaFinancieraId Identificador único de la cuenta.
     * @param estado             Estado de transacción a filtrar.
     * @param pageable           Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} con las transacciones que coinciden con el estado indicado.
     */
    Page<Transacciones> findByCuentaFinancieraIdAndEstado(Long cuentaFinancieraId, EstadoTransaccion estado,
                                                          Pageable pageable);

    /**
     * Recupera de forma paginada las transacciones de una cuenta registradas dentro de un rango de fechas.
     *
     * @param cuentaFinancieraId Identificador único de la cuenta.
     * @param desde              Fecha y hora de inicio del rango (inclusive).
     * @param hasta              Fecha y hora de fin del rango (inclusive).
     * @param pageable           Número de página, tamaño y criterio de ordenamiento.
     * @return {@link Page} con las transacciones comprendidas en el rango.
     */
    Page<Transacciones> findByCuentaFinancieraIdAndFechaBetween(Long cuentaFinancieraId, Date desde, Date hasta,
                                                                Pageable pageable);
}

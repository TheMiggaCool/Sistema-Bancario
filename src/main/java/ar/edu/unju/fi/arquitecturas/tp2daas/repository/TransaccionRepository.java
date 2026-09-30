package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Transaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link Transaccion}.
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // ==========================================
    // Consultas paginadas
    // ==========================================

    /**
     * Recupera de forma paginada el historial de transacciones de una cuenta.
     * El orden (ej. fecha descendente) se define mediante el objeto {@link Pageable}.
     *
     * @param cuentaId Identificador único de la cuenta bancaria.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página de transacciones de la cuenta.
     */
    Page<Transaccion> findByCuentaBancariaId(Long cuentaId, Pageable pageable);

    /**
     * Recupera de forma paginada las transacciones de una cuenta filtradas por tipo de operación.
     *
     * @param cuentaId Identificador único de la cuenta bancaria.
     * @param tipo     Tipo de transacción a filtrar.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con las transacciones que coinciden con el tipo indicado.
     */
    Page<Transaccion> findByCuentaBancariaIdAndTipo(Long cuentaId, TipoTransaccion tipo, Pageable pageable);

    /**
     * Recupera de forma paginada las transacciones de una cuenta filtradas por su estado.
     *
     * @param cuentaId Identificador único de la cuenta bancaria.
     * @param estado   Estado de la transacción a filtrar.
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con las transacciones que coinciden con el estado indicado.
     */
    Page<Transaccion> findByCuentaBancariaIdAndEstado(Long cuentaId, EstadoTransaccion estado, Pageable pageable);

    /**
     * Recupera de forma paginada el extracto bancario de una cuenta dentro de un rango temporal.
     *
     * @param cuentaId Identificador único de la cuenta bancaria.
     * @param desde    Fecha inicial del período (inclusive).
     * @param hasta    Fecha final del período (inclusive).
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página con las transacciones comprendidas en el rango de fechas.
     */
    Page<Transaccion> findByCuentaBancariaIdAndFechaBetween(Long cuentaId, Date desde, Date hasta, Pageable pageable);


    // ==========================================
    // Consultas directas no paginadas
    // ==========================================

    /**
     * Recupera la totalidad de transacciones de una cuenta ordenadas cronológicamente de forma descendente.
     *
     * @param cuentaId Identificador de la cuenta bancaria.
     * @return Lista de transacciones asociadas a la cuenta.
     */
    List<Transaccion> findByCuentaBancariaIdOrderByFechaDesc(Long cuentaId);


    // ==========================================
    // Consultas optimizadas con JOIN FETCH
    // ==========================================

    /**
     * Busca una transacción por su ID cargando ansiosamente (Fetch Join) los datos de la cuenta asociada y su cliente.
     * Previene LazyInitializationException al emitir comprobantes o auditar movimientos.
     *
     * @param id Identificador único de la transacción.
     * @return {@link Optional} con la transacción y sus entidades relacionadas inicializadas.
     */
    @Query("SELECT t FROM Transaccion t " +
            "JOIN FETCH t.cuentaBancaria c " +
            "JOIN FETCH c.cliente " +
            "WHERE t.id = :id")
    Optional<Transaccion> findByIdWithCuentaAndCliente(@Param("id") Long id);
}
package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Transaccion;
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

    /**
     * Recupera todas las transacciones asociadas a una cuenta bancaria, ordenadas cronológicamente de forma descendente.
     *
     * @param cuentaId Identificador de la cuenta bancaria.
     * @return Lista de transacciones asociadas a la cuenta.
     */
    List<Transaccion> findByCuentaBancariaIdOrderByFechaDesc(Long cuentaId);

    /**
     * Recupera las transacciones de una cuenta filtradas por su tipo (ej. DEPOSITO, EXTRACCION, TRANSFERENCIA).
     *
     * @param cuentaId Identificador de la cuenta.
     * @param tipo     Tipo de transacción.
     * @return Lista de transacciones que coinciden con el tipo solicitado.
     */
    List<Transaccion> findByCuentaBancariaIdAndTipo(Long cuentaId, TipoTransaccion tipo);

    /**
     * Recupera las transacciones de una cuenta filtradas por su estado (ej. APROBADA, RECHAZADA, PENDIENTE).
     *
     * @param cuentaId Identificador de la cuenta.
     * @param estado   Estado de la transacción.
     * @return Lista de transacciones que coinciden con el estado solicitado.
     */
    List<Transaccion> findByCuentaBancariaIdAndEstado(Long cuentaId, EstadoTransaccion estado);

    /**
     * Recupera el historial de movimientos de una cuenta dentro de un rango de fechas determinado (extracto bancario).
     *
     * @param cuentaId    Identificador de la cuenta.
     * @param fechaInicio Fecha inicial del rango.
     * @param fechaFin    Fecha final del rango.
     * @return Lista de transacciones comprendidas en el rango de fechas.
     */
    List<Transaccion> findByCuentaBancariaIdAndFechaBetweenOrderByFechaDesc(Long cuentaId, Date fechaInicio, Date fechaFin);

    /**
     * Busca una transacción por su ID cargando ansiosamente (Fetch Join) los datos de la cuenta asociada y su cliente.
     * Evita consultas adicionales y problemas de carga perezosa (LazyInitializationException) al emitir comprobantes.
     *
     * @param id Identificador único de la transacción.
     * @return {@link Optional} con la transacción y su grafo de cuenta/cliente inicializado.
     */
    @Query("SELECT t FROM Transaccion t " +
            "JOIN FETCH t.cuentaBancaria c " +
            "JOIN FETCH c.cliente " +
            "WHERE t.id = :id")
    Optional<Transaccion> findByIdWithCuentaAndCliente(@Param("id") Long id);
}
package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link CuentaBancaria}.
 * Aplica sobre la jerarquía de cuentas (incluyendo CajaDeAhorro y CuentaCorriente).
 */
@Repository
public interface CuentaRepository extends JpaRepository<CuentaBancaria, Long> {

    /**
     * Busca una cuenta por su CBU (Clave Bancaria Uniforme).
     *
     * @param cbu CBU de 22 dígitos.
     * @return {@link Optional} con la cuenta si existe.
     */
    Optional<CuentaBancaria> findByCBU(String cbu);

    /**
     * Busca una cuenta bancaria por su alias.
     *
     * @param alias Alias de la cuenta.
     * @return {@link Optional} con la cuenta si existe.
     */
    Optional<CuentaBancaria> findByAlias(String alias);

    /**
     * Comprueba si ya existe una cuenta con el CBU o Alias indicado.
     * Útil para validaciones previas a la creación/actualización de cuentas.
     *
     * @param cbu   CBU a verificar.
     * @param alias Alias a verificar.
     * @return true si ya existe una cuenta registrada con ese CBU o alias.
     */
    boolean existsByCBUOrAlias(String cbu, String alias);

    /**
     * Obtiene todas las cuentas bancarias pertenecientes a un cliente específico.
     *
     * @param clienteId Identificador del cliente.
     * @return Lista de cuentas asociadas al cliente.
     */
    List<CuentaBancaria> findByClienteId(Long clienteId);

    /**
     * Obtiene las cuentas de un cliente filtradas por su estado (por ejemplo, ACTIVAS).
     *
     * @param clienteId Identificador del cliente.
     * @param estado    Estado de la cuenta.
     * @return Lista de cuentas del cliente con el estado indicado.
     */
    List<CuentaBancaria> findByClienteIdAndEstado(Long clienteId, EstadoCuenta estado);

    /**
     * Recupera una cuenta bancaria junto con sus transacciones cargadas de forma ansiosa (Fetch Join)
     * para evitar el problema de N+1 consultas al consultar el historial de movimientos.
     *
     * @param id Identificador único de la cuenta.
     * @return {@link Optional} de la cuenta con su colección de transacciones inicializada.
     */
    @Query("SELECT c FROM CuentaBancaria c LEFT JOIN FETCH c.transacciones WHERE c.id = :id")
    Optional<CuentaBancaria> findByIdWithTransacciones(@Param("id") Long id);

    /**
     * Recupera una cuenta bancaria por su CBU cargando sus transacciones.
     *
     * @param cbu CBU de la cuenta.
     * @return {@link Optional} de la cuenta con su colección de transacciones inicializada.
     */
    @Query("SELECT c FROM CuentaBancaria c LEFT JOIN FETCH c.transacciones WHERE c.CBU = :cbu")
    Optional<CuentaBancaria> findByCBUWithTransacciones(@Param("cbu") String cbu);
}
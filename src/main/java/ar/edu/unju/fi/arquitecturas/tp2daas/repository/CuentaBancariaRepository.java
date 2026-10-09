package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaBancaria;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
public interface CuentaBancariaRepository extends JpaRepository<CuentaBancaria, Long> {

    // ==========================================
    // Búsquedas básicas por identificadores únicos
    // ==========================================

    /**
     * Busca una cuenta por su CBU (Clave Bancaria Uniforme).
     *
     * @param CBU CBU de 22 dígitos.
     * @return {@link Optional} con la cuenta si existe.
     */
    Optional<CuentaBancaria> findByCBU(String CBU);

    /**
     * Busca una cuenta bancaria por su alias.
     *
     * @param alias Alias de la cuenta.
     * @return {@link Optional} con la cuenta si existe.
     */
    Optional<CuentaBancaria> findByAlias(String alias);


    // ==========================================
    // Validaciones de unicidad (Creación y Edición)
    // ==========================================

    /**
     * Comprueba si ya existe una cuenta con el CBU o Alias indicado al dar de alta.
     *
     * @param CBU   CBU a verificar.
     * @param alias Alias a verificar.
     * @return true si ya existe una cuenta registrada con ese CBU o alias.
     */
    boolean existsByCBUOrAlias(String CBU, String alias);

    /**
     * Verifica si otra cuenta distinta a la indicada ya utiliza el alias recibido.
     * Se emplea al actualizar datos para no bloquear la edición si el alias no cambió.
     *
     * @param alias Alias a validar.
     * @param id    Identificador de la cuenta que se excluye de la comprobación.
     * @return true si el alias pertenece a otra cuenta.
     */
    boolean existsByAliasAndIdNot(String alias, Long id);


    // ==========================================
    // Consultas asociadas al Cliente
    // ==========================================

    /**
     * Obtiene todas las cuentas bancarias pertenecientes a un cliente.
     *
     * @param clienteId Identificador único del cliente.
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


    // ==========================================
    // Concurrencia y Bloqueos (Crítico para saldos)
    // ==========================================

    /**
     * Recupera una cuenta aplicando un bloqueo pesimista de escritura (SELECT ... FOR UPDATE),
     * impidiendo que dos hilos o transacciones modifiquen el saldo en paralelo.
     * Debe invocarse dentro de un método transaccional (@Transactional).
     *
     * @param id Identificador único de la cuenta.
     * @return {@link Optional} con la cuenta bloqueada si existe.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaBancaria c WHERE c.id = :id")
    Optional<CuentaBancaria> findByIdForUpdate(@Param("id") Long id);

    /**
     * Bloqueo pesimista de escritura buscando directamente por CBU.
     *
     * @param CBU Clave bancaria uniforme.
     * @return {@link Optional} con la cuenta bloqueada si existe.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaBancaria c WHERE c.CBU = :CBU")
    Optional<CuentaBancaria> findByCBUForUpdate(@Param("cbu") String CBU);


    // ==========================================
    // Cargas ansiosas con LEFT JOIN FETCH
    // ==========================================

    /**
     * Recupera una cuenta bancaria junto con sus transacciones (Fetch Join)
     * evitando consultas N+1 y problemas de sesión al leer su historial.
     *
     * @param id Identificador único de la cuenta.
     * @return {@link Optional} de la cuenta con su colección de transacciones inicializada.
     */
    @Query("SELECT c FROM CuentaBancaria c LEFT JOIN FETCH c.transacciones WHERE c.id = :id")
    Optional<CuentaBancaria> findByIdWithTransacciones(@Param("id") Long id);

    /**
     * Recupera una cuenta por CBU cargando ansiosamente sus transacciones.
     *
     * @param CBU CBU de la cuenta.
     * @return {@link Optional} de la cuenta con su colección de transacciones inicializada.
     */
    @Query("SELECT c FROM CuentaBancaria c LEFT JOIN FETCH c.transacciones WHERE c.CBU = :CBU")
    Optional<CuentaBancaria> findByCBUWithTransacciones(@Param("cbu") String CBU);

    /**
     * Obtiene únicamente los identificadores de las cuentas en un estado dado.
     * Se usa en procesos masivos para no cargar todas las entidades en memoria.
     */
    @Query("SELECT c.id FROM CuentaBancaria c WHERE c.estado = :estado")
    List<Long> findIdsByEstado(@Param("estado") EstadoCuenta estado);
}
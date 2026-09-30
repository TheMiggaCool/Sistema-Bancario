package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentasFinancieras;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link CuentasFinancieras}.
 */
@Repository
public interface CuentasFinancierasRepository extends JpaRepository<CuentasFinancieras, Long> {

    /**
     * Busca una cuenta por su Clave Bancaria Uniforme (CBU).
     *
     * @param cbu CBU de 22 dígitos.
     * @return {@link Optional} con la cuenta si existe.
     */
    @Query("SELECT c FROM CuentasFinancieras c WHERE c.CBU = :cbu")
    Optional<CuentasFinancieras> findByCbu(@Param("cbu") String cbu);

    /**
     * Busca una cuenta por su alias.
     *
     * @param alias Alias de la cuenta.
     * @return {@link Optional} con la cuenta si existe.
     */
    Optional<CuentasFinancieras> findByAlias(String alias);

    /**
     * Verifica la existencia de una cuenta por CBU o Alias para validaciones de unicidad.
     *
     * @param cbu   CBU a validar.
     * @param alias Alias a validar.
     * @return true si existe al menos un registro que coincida con alguno de los datos.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CuentasFinancieras c "
            + "WHERE c.CBU = :cbu OR c.alias = :alias")
    boolean existsByCbuOrAlias(@Param("cbu") String cbu, @Param("alias") String alias);

    /**
     * Verifica si otra cuenta distinta a la indicada ya utiliza el alias recibido.
     * Se emplea en las actualizaciones para validar la unicidad del alias.
     *
     * @param alias Alias a validar.
     * @param id    Identificador de la cuenta que se excluye de la comprobación.
     * @return true si el alias pertenece a otra cuenta.
     */
    boolean existsByAliasAndIdNot(String alias, Long id);

    /**
     * Recupera todas las cuentas pertenecientes a un cliente.
     *
     * @param clienteId Identificador único del cliente titular.
     * @return {@link List} con las cuentas del cliente; vacía si no posee ninguna.
     */
    List<CuentasFinancieras> findByClienteId(Long clienteId);

    /**
     * Recupera una cuenta aplicando un bloqueo pesimista de escritura sobre su registro,
     * para evitar actualizaciones concurrentes del saldo (lecturas desactualizadas)
     * mientras se registra una transacción.
     * Debe invocarse dentro de una transacción activa.
     *
     * @param id Identificador único de la cuenta.
     * @return {@link Optional} con la cuenta bloqueada si existe.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentasFinancieras c WHERE c.id = :id")
    Optional<CuentasFinancieras> findByIdForUpdate(@Param("id") Long id);
}

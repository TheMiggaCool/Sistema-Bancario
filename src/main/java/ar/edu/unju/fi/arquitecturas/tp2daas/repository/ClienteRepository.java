package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link Cliente}.
 * <p>
 * Las consultas con sufijo {@code WithCuentas} inicializan la colección de cuentas mediante
 * Fetch Join, de modo que el servicio pueda construir el DTO de salida (que informa los
 * identificadores de las cuentas) sin disparar una consulta adicional por cada cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un cliente por su número de CUIL.
     *
     * @param cuil Clave Única de Identificación Laboral / Tributaria.
     * @return {@link Optional} con el cliente si existe.
     */
    Optional<Cliente> findByCuil(String cuil);

    /**
     * Busca un cliente por su dirección de correo electrónico.
     *
     * @param email Dirección de e-mail.
     * @return {@link Optional} con el cliente si existe.
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Verifica la existencia de un cliente por CUIL o Email para validaciones de unicidad.
     *
     * @param cuil  CUIL a validar.
     * @param email Email a validar.
     * @return true si existe al menos un registro que coincida con alguno de los datos.
     */
    boolean existsByCuilOrEmail(String cuil, String email);

    /**
     * Verifica si otro cliente distinto al indicado ya utiliza el email recibido.
     * Se emplea en las actualizaciones para validar la unicidad del email.
     *
     * @param email Email a validar.
     * @param id    Identificador del cliente que se excluye de la comprobación.
     * @return true si el email pertenece a otro cliente.
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Recupera un cliente junto con sus cuentas asociadas cargadas de forma ansiosa (Fetch Join)
     * para evitar el problema de N+1 consultas en lecturas completas del grafo.
     *
     * @param id Identificador único del cliente.
     * @return {@link Optional} del cliente con la colección de cuentas inicializada.
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.cuentaBancaria WHERE c.id = :id")
    Optional<Cliente> findByIdWithCuentas(@Param("id") Long id);

    /**
     * Recupera un cliente por su CUIL junto con sus cuentas asociadas cargadas de forma ansiosa
     * (Fetch Join).
     *
     * @param cuil Clave Única de Identificación Laboral / Tributaria.
     * @return {@link Optional} del cliente con la colección de cuentas inicializada.
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.cuentaBancaria WHERE c.cuil = :cuil")
    Optional<Cliente> findByCuilWithCuentas(@Param("cuil") String cuil);

    /**
     * Recupera la totalidad de los clientes junto con sus cuentas asociadas cargadas de forma
     * ansiosa (Fetch Join), evitando una consulta adicional por cada cliente listado.
     *
     * @return {@link List} con todos los clientes y sus cuentas inicializadas; vacía si no hay registros.
     */
    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.cuentaBancaria")
    List<Cliente> findAllWithCuentas();
}

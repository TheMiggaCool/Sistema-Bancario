package ar.edu.unju.fi.arquitecturas.tp2daas.repository;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link Cliente}.
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
     * Recupera un cliente junto con sus cuentas asociadas cargadas de forma ansiosa (Fetch Join)
     * para evitar el problema de N+1 consultas en lecturas completas del grafo.
     *
     * @param id Identificador único del cliente.
     * @return {@link Optional} del cliente con la colección de cuentas inicializada.
     */
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.cuentaBancaria WHERE c.id = :id")
    Optional<Cliente> findByIdWithCuentas(@Param("id") Long id);
}
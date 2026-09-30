package ar.edu.unju.fi.arquitecturas.tp2daas.service;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.CuentasFinancierasRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.CuentasFinancierasResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;

import java.util.List;

/**
 * Servicio con la lógica de negocio para la gestión de cuentas bancarias.
 */
public interface CuentasFinancierasService {

    /**
     * Crea una cuenta bancaria para un cliente existente. La cuenta se abre en estado
     * {@link EstadoCuenta#ACTIVA} con el saldo inicial indicado.
     *
     * @param request Datos de la cuenta a crear.
     * @return DTO con la cuenta registrada.
     */
    CuentasFinancierasResponseDTO crearCuenta(CuentasFinancierasRequestDTO request);

    /**
     * Obtiene una cuenta por su identificador.
     *
     * @param id Identificador único de la cuenta.
     * @return DTO con la cuenta encontrada.
     */
    CuentasFinancierasResponseDTO obtenerPorId(Long id);

    /**
     * Obtiene una cuenta por su CBU.
     *
     * @param cbu CBU de 22 dígitos.
     * @return DTO con la cuenta encontrada.
     */
    CuentasFinancierasResponseDTO obtenerPorCbu(String cbu);

    /**
     * Lista la totalidad de las cuentas registradas.
     *
     * @return {@link List} de DTO con todas las cuentas.
     */
    List<CuentasFinancierasResponseDTO> listarTodas();

    /**
     * Lista las cuentas pertenecientes a un cliente.
     *
     * @param clienteId Identificador único del cliente titular.
     * @return {@link List} de DTO con las cuentas del cliente.
     */
    List<CuentasFinancierasResponseDTO> listarPorCliente(Long clienteId);

    /**
     * Actualiza el alias y los parámetros propios del tipo de cuenta. El CBU, el titular
     * y el saldo no se modifican; el saldo solo varía al registrar transacciones.
     *
     * @param id      Identificador único de la cuenta a actualizar.
     * @param request Nuevos datos; los campos {@code null} conservan su valor actual.
     * @return DTO con la cuenta actualizada.
     */
    CuentasFinancierasResponseDTO actualizarCuenta(Long id, CuentasFinancierasRequestDTO request);

    /**
     * Cambia el estado operativo de una cuenta (por ejemplo, para suspenderla o bloquearla).
     *
     * @param id          Identificador único de la cuenta.
     * @param nuevoEstado Nuevo {@link EstadoCuenta}.
     * @return DTO con la cuenta actualizada.
     */
    CuentasFinancierasResponseDTO cambiarEstado(Long id, EstadoCuenta nuevoEstado);

    /**
     * Elimina una cuenta por su identificador.
     *
     * @param id Identificador único de la cuenta a eliminar.
     */
    void eliminarPorId(Long id);
}

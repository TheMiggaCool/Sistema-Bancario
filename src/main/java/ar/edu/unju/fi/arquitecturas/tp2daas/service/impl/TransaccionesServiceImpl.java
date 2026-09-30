package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.TransaccionesRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.TransaccionesResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaCorriete;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentasFinancieras;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Transacciones;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.CuentasFinancierasRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.TransaccionesRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.TransaccionesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransaccionesServiceImpl implements TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;
    private final CuentasFinancierasRepository cuentasFinancierasRepository;

    @Override
    @Transactional
    public TransaccionesResponseDTO registrarTransaccion(TransaccionesRequestDTO request) {
        log.info("Iniciando registro de transacción de tipo {} por un monto de {} sobre la cuenta con ID: {}",
                request.getTipo(), request.getMonto(), request.getCuentaFinancieraId());

        validarDatosTransaccion(request);

        return switch (request.getTipo()) {
            case DEPOSITO -> registrarDeposito(request);
            case EXTRACCION -> registrarExtraccion(request);
            case TRANSFERENCIA_ENVIADA -> registrarTransferencia(request);
            case TRANSFERENCIA_RECIBIDA -> throw new IllegalArgumentException(
                    "Las transferencias recibidas se generan automáticamente al registrar una transferencia enviada.");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public TransaccionesResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando transacción por ID: {}", id);
        return transaccionesRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionesResponseDTO> listarTodas(Pageable pageable) {
        log.debug("Listando transacciones. Página: {}, tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return transaccionesRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionesResponseDTO> listarPorCuenta(Long cuentaId, Pageable pageable) {
        log.debug("Listando transacciones de la cuenta {}. Página: {}, tamaño: {}",
                cuentaId, pageable.getPageNumber(), pageable.getPageSize());
        validarExistenciaCuenta(cuentaId);
        return transaccionesRepository.findByCuentaFinancieraId(cuentaId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionesResponseDTO> listarPorCuentaYTipo(Long cuentaId, TipoTransaccion tipo,
                                                               Pageable pageable) {
        log.debug("Listando transacciones de tipo {} de la cuenta {}. Página: {}, tamaño: {}",
                tipo, cuentaId, pageable.getPageNumber(), pageable.getPageSize());
        validarExistenciaCuenta(cuentaId);
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de transacción es obligatorio.");
        }
        return transaccionesRepository.findByCuentaFinancieraIdAndTipo(cuentaId, tipo, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionesResponseDTO> listarPorCuentaYEstado(Long cuentaId, EstadoTransaccion estado,
                                                                 Pageable pageable) {
        log.debug("Listando transacciones en estado {} de la cuenta {}. Página: {}, tamaño: {}",
                estado, cuentaId, pageable.getPageNumber(), pageable.getPageSize());
        validarExistenciaCuenta(cuentaId);
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la transacción es obligatorio.");
        }
        return transaccionesRepository.findByCuentaFinancieraIdAndEstado(cuentaId, estado, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionesResponseDTO> listarPorCuentaEntreFechas(Long cuentaId, Date desde, Date hasta,
                                                                     Pageable pageable) {
        log.debug("Listando transacciones de la cuenta {} entre {} y {}. Página: {}, tamaño: {}",
                cuentaId, desde, hasta, pageable.getPageNumber(), pageable.getPageSize());
        validarExistenciaCuenta(cuentaId);
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin del rango son obligatorias.");
        }
        if (desde.after(hasta)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        return transaccionesRepository.findByCuentaFinancieraIdAndFechaBetween(cuentaId, desde, hasta, pageable)
                .map(this::toResponse);
    }

    // ------------------------------------------------------------------
    // Operaciones de negocio
    // ------------------------------------------------------------------

    private TransaccionesResponseDTO registrarDeposito(TransaccionesRequestDTO request) {
        CuentasFinancieras cuenta = obtenerCuentaActivaBloqueada(request.getCuentaFinancieraId());

        cuenta.setSaldo(cuenta.getSaldo() + request.getMonto());
        cuentasFinancierasRepository.save(cuenta);

        Transacciones transaccion = guardarTransaccion(cuenta, TipoTransaccion.DEPOSITO, request.getMonto());

        log.info("Depósito registrado exitosamente con ID: {}", transaccion.getId());
        return toResponse(transaccion);
    }

    private TransaccionesResponseDTO registrarExtraccion(TransaccionesRequestDTO request) {
        CuentasFinancieras cuenta = obtenerCuentaActivaBloqueada(request.getCuentaFinancieraId());

        validarFondosSuficientes(cuenta, request.getMonto());

        cuenta.setSaldo(cuenta.getSaldo() - request.getMonto());
        cuentasFinancierasRepository.save(cuenta);

        Transacciones transaccion = guardarTransaccion(cuenta, TipoTransaccion.EXTRACCION, request.getMonto());

        log.info("Extracción registrada exitosamente con ID: {}", transaccion.getId());
        return toResponse(transaccion);
    }

    private TransaccionesResponseDTO registrarTransferencia(TransaccionesRequestDTO request) {
        Long origenId = request.getCuentaFinancieraId();
        Long destinoId = request.getCuentaDestinoId();

        if (destinoId == null) {
            throw new IllegalArgumentException("La cuenta destino es obligatoria para registrar una transferencia.");
        }
        if (origenId.equals(destinoId)) {
            throw new IllegalArgumentException("La cuenta origen y la cuenta destino deben ser distintas.");
        }

        // Se bloquean ambas cuentas siempre en orden ascendente de ID para evitar bloqueos mutuos (deadlocks)
        CuentasFinancieras origen;
        CuentasFinancieras destino;
        if (origenId < destinoId) {
            origen = obtenerCuentaActivaBloqueada(origenId);
            destino = obtenerCuentaActivaBloqueada(destinoId);
        } else {
            destino = obtenerCuentaActivaBloqueada(destinoId);
            origen = obtenerCuentaActivaBloqueada(origenId);
        }

        validarFondosSuficientes(origen, request.getMonto());

        origen.setSaldo(origen.getSaldo() - request.getMonto());
        destino.setSaldo(destino.getSaldo() + request.getMonto());
        cuentasFinancierasRepository.save(origen);
        cuentasFinancierasRepository.save(destino);

        Transacciones enviada = guardarTransaccion(origen, TipoTransaccion.TRANSFERENCIA_ENVIADA, request.getMonto());
        Transacciones recibida = guardarTransaccion(destino, TipoTransaccion.TRANSFERENCIA_RECIBIDA, request.getMonto());

        log.info("Transferencia registrada exitosamente. Enviada ID: {}, Recibida ID: {}",
                enviada.getId(), recibida.getId());
        return toResponse(enviada);
    }

    /*
        ------------------------------------------------------------------
        Métodos auxiliares
        ------------------------------------------------------------------
    */

    private void validarDatosTransaccion(TransaccionesRequestDTO request) {
        if (request.getCuentaFinancieraId() == null) {
            throw new IllegalArgumentException("El ID de la cuenta es obligatorio.");
        }
        if (request.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de transacción es obligatorio.");
        }
        // La negación cubre también el caso NaN
        if (!(request.getMonto() > 0) || Double.isInfinite(request.getMonto())) {
            throw new IllegalArgumentException("El monto de la transacción debe ser mayor a cero.");
        }
    }

    private void validarExistenciaCuenta(Long cuentaId) {
        if (cuentaId == null || !cuentasFinancierasRepository.existsById(cuentaId)) {
            throw new IllegalArgumentException("Cuenta no encontrada con el ID: " + cuentaId);
        }
    }

    private CuentasFinancieras obtenerCuentaActivaBloqueada(Long cuentaId) {
        CuentasFinancieras cuenta = cuentasFinancierasRepository.findByIdForUpdate(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el ID: " + cuentaId));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            log.error("Operación rechazada. La cuenta {} se encuentra en estado {}", cuentaId, cuenta.getEstado());
            throw new IllegalStateException("La cuenta con ID " + cuentaId
                    + " no permite operar porque se encuentra " + cuenta.getEstado() + ".");
        }
        return cuenta;
    }

    private void validarFondosSuficientes(CuentasFinancieras cuenta, double monto) {
        // En cuentas corrientes se admite operar hasta el margen de descubierto autorizado
        double margen = (cuenta instanceof CuentaCorriete cuentaCorriente) ? cuentaCorriente.getMargenAutorizado() : 0.0;
        double disponible = cuenta.getSaldo() + margen;

        if (monto > disponible) {
            log.error("Fondos insuficientes en la cuenta {}. Disponible: {}, requerido: {}",
                    cuenta.getId(), disponible, monto);
            throw new IllegalStateException("Fondos insuficientes en la cuenta con ID " + cuenta.getId() + ".");
        }
    }

    private Transacciones guardarTransaccion(CuentasFinancieras cuenta, TipoTransaccion tipo, double monto) {
        Transacciones transaccion = Transacciones.builder()
                .fecha(new Date())
                .monto(monto)
                .tipo(tipo)
                .estado(EstadoTransaccion.COMPLETADA)
                .cuentaFinanciera(cuenta)
                .build();
        return transaccionesRepository.save(transaccion);
    }

    private TransaccionesResponseDTO toResponse(Transacciones transaccion) {
        return TransaccionesResponseDTO.builder()
                .id(transaccion.getId())
                .fecha(transaccion.getFecha())
                .monto(transaccion.getMonto())
                .tipo(transaccion.getTipo())
                .estado(transaccion.getEstado())
                .cuentaFinancieraId(transaccion.getCuentaFinanciera().getId())
                .build();
    }
}

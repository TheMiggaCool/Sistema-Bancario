package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.TransaccionRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.TransaccionResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaCorriente;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoTransaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Transaccion;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.TransaccionRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.TransaccionService;
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
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;

    @Override
    @Transactional
    public TransaccionResponseDTO registrarTransaccion(TransaccionRequestDTO request) {
        log.info("Iniciando registro de transacción tipo {} por monto {} en cuenta ID: {}",
                request.getTipo(), request.getMonto(), request.getCuentaBancariaId());

        validarDatosTransaccion(request);

        return switch (request.getTipo()) {
            case DEPOSITO -> registrarDeposito(request);
            case EXTRACCION -> registrarExtraccion(request);
            case TRANSFERENCIA_ENVIADA -> registrarTransferencia(request);
            case TRANSFERENCIA_RECIBIDA -> throw new IllegalArgumentException(
                    "Las transferencias recibidas se generan automáticamente al procesar una transferencia enviada.");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public TransaccionResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando transacción por ID: {}", id);
        return transaccionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionResponseDTO> listarTodas(Pageable pageable) {
        log.debug("Listando transacciones. Página: {}, tamaño: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return transaccionRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionResponseDTO> listarPorCuenta(Long cuentaId, Pageable pageable) {
        log.debug("Listando transacciones de la cuenta ID: {}", cuentaId);
        validarExistenciaCuenta(cuentaId);
        return transaccionRepository.findByCuentaBancariaId(cuentaId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionResponseDTO> listarPorCuentaYTipo(Long cuentaId, TipoTransaccion tipo, Pageable pageable) {
        log.debug("Listando transacciones de tipo {} para cuenta ID: {}", tipo, cuentaId);
        validarExistenciaCuenta(cuentaId);
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de transacción es obligatorio.");
        }
        return transaccionRepository.findByCuentaBancariaIdAndTipo(cuentaId, tipo, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionResponseDTO> listarPorCuentaYEstado(Long cuentaId, EstadoTransaccion estado, Pageable pageable) {
        log.debug("Listando transacciones en estado {} para cuenta ID: {}", estado, cuentaId);
        validarExistenciaCuenta(cuentaId);
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la transacción es obligatorio.");
        }
        return transaccionRepository.findByCuentaBancariaIdAndEstado(cuentaId, estado, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransaccionResponseDTO> listarPorCuentaEntreFechas(Long cuentaId, Date desde, Date hasta, Pageable pageable) {
        log.debug("Listando transacciones de cuenta ID: {} entre {} y {}", cuentaId, desde, hasta);
        validarExistenciaCuenta(cuentaId);
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin del rango son obligatorias.");
        }
        if (desde.after(hasta)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        return transaccionRepository.findByCuentaBancariaIdAndFechaBetween(cuentaId, desde, hasta, pageable)
                .map(this::toResponse);
    }

    // ==========================================
    // Operaciones de negocio
    // ==========================================

    private TransaccionResponseDTO registrarDeposito(TransaccionRequestDTO request) {
        CuentaBancaria cuenta = obtenerCuentaActivaBloqueada(request.getCuentaBancariaId());

        cuenta.setSaldo(cuenta.getSaldo() + request.getMonto());
        cuentaBancariaRepository.save(cuenta);

        Transaccion transaccion = guardarTransaccion(cuenta, TipoTransaccion.DEPOSITO, request.getMonto());
        log.info("Depósito registrado con ID: {}", transaccion.getId());
        return toResponse(transaccion);
    }

    private TransaccionResponseDTO registrarExtraccion(TransaccionRequestDTO request) {
        CuentaBancaria cuenta = obtenerCuentaActivaBloqueada(request.getCuentaBancariaId());

        validarFondosSuficientes(cuenta, request.getMonto());

        cuenta.setSaldo(cuenta.getSaldo() - request.getMonto());
        cuentaBancariaRepository.save(cuenta);

        Transaccion transaccion = guardarTransaccion(cuenta, TipoTransaccion.EXTRACCION, request.getMonto());
        log.info("Extracción registrada con ID: {}", transaccion.getId());
        return toResponse(transaccion);
    }

    private TransaccionResponseDTO registrarTransferencia(TransaccionRequestDTO request) {
        Long origenId = request.getCuentaBancariaId();
        Long destinoId = request.getCuentaDestinoId();

        if (destinoId == null) {
            throw new IllegalArgumentException("La cuenta destino es obligatoria para realizar una transferencia.");
        }
        if (origenId.equals(destinoId)) {
            throw new IllegalArgumentException("La cuenta de origen y destino no pueden ser la misma.");
        }

        // Bloqueo pesimista en orden ascendente de ID para evitar deadlocks en concurrencia
        CuentaBancaria origen;
        CuentaBancaria destino;
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
        cuentaBancariaRepository.save(origen);
        cuentaBancariaRepository.save(destino);

        Transaccion enviada = guardarTransaccion(origen, TipoTransaccion.TRANSFERENCIA_ENVIADA, request.getMonto());
        Transaccion recibida = guardarTransaccion(destino, TipoTransaccion.TRANSFERENCIA_RECIBIDA, request.getMonto());

        log.info("Transferencia registrada exitosamente. Enviada ID: {}, Recibida ID: {}",
                enviada.getId(), recibida.getId());
        return toResponse(enviada);
    }

    // ==========================================
    // Métodos auxiliares y validaciones
    // ==========================================

    private void validarDatosTransaccion(TransaccionRequestDTO request) {
        if (request.getCuentaBancariaId() == null) {
            throw new IllegalArgumentException("El ID de la cuenta es obligatorio.");
        }
        if (request.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de transacción es obligatorio.");
        }
        if (!(request.getMonto() > 0) || Double.isInfinite(request.getMonto())) {
            throw new IllegalArgumentException("El monto de la transacción debe ser mayor a cero.");
        }
    }

    private void validarExistenciaCuenta(Long cuentaId) {
        if (cuentaId == null || !cuentaBancariaRepository.existsById(cuentaId)) {
            throw new IllegalArgumentException("Cuenta no encontrada con el ID: " + cuentaId);
        }
    }

    private CuentaBancaria obtenerCuentaActivaBloqueada(Long cuentaId) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findByIdForUpdate(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el ID: " + cuentaId));

        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            log.error("Operación rechazada. La cuenta {} se encuentra {}", cuentaId, cuenta.getEstado());
            throw new IllegalStateException("La cuenta con ID " + cuentaId
                    + " no puede operar porque se encuentra " + cuenta.getEstado() + ".");
        }
        return cuenta;
    }

    private void validarFondosSuficientes(CuentaBancaria cuenta, double monto) {
        // En cuentas corrientes se admite operar hasta el margen de descubierto autorizado
        double margen = (cuenta instanceof CuentaCorriente cuentaCorriente) ? cuentaCorriente.getMargenAutorizado() : 0.0;
        double disponible = cuenta.getSaldo() + margen;

        if (monto > disponible) {
            log.error("Fondos insuficientes en la cuenta {}. Disponible: {}, requerido: {}",
                    cuenta.getId(), disponible, monto);
            throw new IllegalStateException("Fondos insuficientes en la cuenta con ID " + cuenta.getId() + ".");
        }
    }

    private Transaccion guardarTransaccion(CuentaBancaria cuenta, TipoTransaccion tipo, double monto) {
        Transaccion transaccion = Transaccion.builder()
                .fecha(new Date())
                .monto(monto)
                .tipo(tipo)
                .estado(EstadoTransaccion.COMPLETADA)
                .cuentaBancaria(cuenta)
                .build();
        return transaccionRepository.save(transaccion);
    }

    private TransaccionResponseDTO toResponse(Transaccion transaccion) {
        return TransaccionResponseDTO.builder()
                .id(transaccion.getId())
                .fecha(transaccion.getFecha())
                .monto(transaccion.getMonto())
                .tipo(transaccion.getTipo())
                .estado(transaccion.getEstado())
                .cuentaBancariaId(transaccion.getCuentaBancaria().getId())
                .build();
    }
}
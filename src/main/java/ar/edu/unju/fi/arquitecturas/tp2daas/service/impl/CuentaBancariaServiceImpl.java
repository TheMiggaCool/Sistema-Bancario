package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.CuentaBancariaRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.CuentaBancariaResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaCorriente;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.CuentaBancariaRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.CuentaBancariaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public CuentaBancariaResponseDTO crearCuenta(CuentaBancariaRequestDTO request) {
        log.info("Iniciando proceso de creación de cuenta con CBU: {}", request.getCBU());

        validarDatosCreacion(request);

        // Se usa existsByCBUOrAlias para coincidir con el repositorio
        if (cuentaBancariaRepository.existsByCBUOrAlias(request.getCBU(), request.getAlias())) {
            log.error("Fallo al crear cuenta. Ya existe un registro con CBU {} o Alias {}",
                    request.getCBU(), request.getAlias());
            throw new IllegalArgumentException("Ya existe una cuenta registrada con el mismo CBU o Alias.");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado con el ID: " + request.getClienteId()));

        CuentaBancaria cuenta = switch (request.getTipoCuenta()) {
            case CAJA_DE_AHORRO -> {
                CajaDeAhorro cajaDeAhorro = new CajaDeAhorro();
                cajaDeAhorro.setTasaInteres(request.getTasaInteres() != null ? request.getTasaInteres() : 0.0);
                cajaDeAhorro.setExtraccionesSinCosto(
                        request.getExtraccionesSinCosto() != null ? request.getExtraccionesSinCosto() : 0);
                yield cajaDeAhorro;
            }
            case CUENTA_CORRIENTE -> {
                CuentaCorriente cuentaCorriente = new CuentaCorriente();
                cuentaCorriente.setMargenAutorizado(
                        request.getMargenAutorizado() != null ? request.getMargenAutorizado() : 0.0);
                cuentaCorriente.setCostoMantenimiento(
                        request.getCostoMantenimiento() != null ? request.getCostoMantenimiento() : 0.0);
                yield cuentaCorriente;
            }
        };

        cuenta.setCBU(request.getCBU());
        cuenta.setAlias(request.getAlias());
        cuenta.setSaldo(request.getSaldoInicial());
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setCliente(cliente);

        CuentaBancaria cuentaGuardada = cuentaBancariaRepository.save(cuenta);

        log.info("Cuenta registrada exitosamente con ID: {}", cuentaGuardada.getId());
        return toResponse(cuentaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaBancariaResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando cuenta por ID: {}", id);
        return toResponse(buscarEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaBancariaResponseDTO obtenerPorCBU(String CBU) {
        log.debug("Buscando cuenta por CBU: {}", CBU);
        // Se usa findByCBU para coincidir con el repositorio
        return cuentaBancariaRepository.findByCBU(CBU)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el CBU: " + CBU));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDTO> listarTodas() {
        log.debug("Listando la totalidad de las cuentas registradas");
        return cuentaBancariaRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaBancariaResponseDTO> listarPorCliente(Long clienteId) {
        log.debug("Listando las cuentas del cliente con ID: {}", clienteId);

        if (!clienteRepository.existsById(clienteId)) {
            throw new IllegalArgumentException("Cliente no encontrado con el ID: " + clienteId);
        }

        return cuentaBancariaRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CuentaBancariaResponseDTO actualizarCuenta(Long id, CuentaBancariaRequestDTO request) {
        log.info("Iniciando actualización de datos para la cuenta con ID: {}", id);

        CuentaBancaria cuentaExistente = buscarEntidadPorId(id);

        if (request.getAlias() != null && !request.getAlias().equals(cuentaExistente.getAlias())) {
            validarAlias(request.getAlias());
            if (cuentaBancariaRepository.existsByAliasAndIdNot(request.getAlias(), id)) {
                log.error("Fallo al actualizar cuenta {}. El alias {} ya está en uso", id, request.getAlias());
                throw new IllegalArgumentException("Ya existe una cuenta registrada con el mismo Alias.");
            }
            cuentaExistente.setAlias(request.getAlias());
        }

        validarParametrosNoNegativos(request);

        if (cuentaExistente instanceof CajaDeAhorro cajaDeAhorro) {
            if (request.getTasaInteres() != null) {
                cajaDeAhorro.setTasaInteres(request.getTasaInteres());
            }
            if (request.getExtraccionesSinCosto() != null) {
                cajaDeAhorro.setExtraccionesSinCosto(request.getExtraccionesSinCosto());
            }
            // Corrección del typo CuentaCorriete -> CuentaCorriente
        } else if (cuentaExistente instanceof CuentaCorriente cuentaCorriente) {
            if (request.getMargenAutorizado() != null) {
                cuentaCorriente.setMargenAutorizado(request.getMargenAutorizado());
            }
            if (request.getCostoMantenimiento() != null) {
                cuentaCorriente.setCostoMantenimiento(request.getCostoMantenimiento());
            }
        }

        return toResponse(cuentaBancariaRepository.save(cuentaExistente));
    }

    @Override
    @Transactional
    public CuentaBancariaResponseDTO cambiarEstado(Long id, EstadoCuenta nuevoEstado) {
        log.info("Solicitado el cambio de estado de la cuenta con ID {} a {}", id, nuevoEstado);

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de la cuenta es obligatorio.");
        }

        CuentaBancaria cuenta = buscarEntidadPorId(id);
        cuenta.setEstado(nuevoEstado);

        return toResponse(cuentaBancariaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        log.info("Solicitada la eliminación de la cuenta con ID: {}", id);
        CuentaBancaria cuenta = buscarEntidadPorId(id);
        cuentaBancariaRepository.delete(cuenta);
        log.info("Cuenta con ID {} eliminada correctamente", id);
    }

    // ==========================================
    // Métodos auxiliares
    // ==========================================

    private CuentaBancaria buscarEntidadPorId(Long id) {
        return cuentaBancariaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el ID: " + id));
    }

    private void validarDatosCreacion(CuentaBancariaRequestDTO request) {
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente titular es obligatorio.");
        }
        if (request.getTipoCuenta() == null) {
            throw new IllegalArgumentException("El tipo de cuenta es obligatorio.");
        }
        if (request.getCBU() == null || !request.getCBU().matches("\\d{22}")) {
            throw new IllegalArgumentException("El CBU debe contener exactamente 22 dígitos numéricos.");
        }
        validarAlias(request.getAlias());
        if (request.getSaldoInicial() < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo.");
        }
        validarParametrosNoNegativos(request);
    }

    private void validarAlias(String alias) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias es obligatorio.");
        }
        if (alias.length() > 50) {
            throw new IllegalArgumentException("El alias no puede superar los 50 caracteres.");
        }
    }

    private void validarParametrosNoNegativos(CuentaBancariaRequestDTO request) {
        validarNoNegativo(request.getTasaInteres(), "tasa de interés");
        validarNoNegativo(request.getExtraccionesSinCosto(), "cantidad de extracciones sin costo");
        validarNoNegativo(request.getMargenAutorizado(), "margen autorizado");
        validarNoNegativo(request.getCostoMantenimiento(), "costo de mantenimiento");
    }

    private void validarNoNegativo(Number valor, String campo) {
        if (valor != null && valor.doubleValue() < 0) {
            throw new IllegalArgumentException("El valor de " + campo + " no puede ser negativo.");
        }
    }

    private CuentaBancariaResponseDTO toResponse(CuentaBancaria cuenta) {
        CuentaBancariaResponseDTO dto = CuentaBancariaResponseDTO.builder()
                .id(cuenta.getId())
                .CBU(cuenta.getCBU())
                .alias(cuenta.getAlias())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getCliente().getId())
                .fechaCreacion(cuenta.getFechaCreacion())
                .fechaUltimaModificacion(cuenta.getFechaUltimaModificacion())
                .build();

        if (cuenta instanceof CajaDeAhorro cajaDeAhorro) {
            dto.setTipoCuenta(TipoCuenta.CAJA_DE_AHORRO);
            dto.setTasaInteres(cajaDeAhorro.getTasaInteres());
            dto.setExtraccionesSinCosto(cajaDeAhorro.getExtraccionesSinCosto());
            // Corrección del typo CuentaCorriete -> CuentaCorriente
        } else if (cuenta instanceof CuentaCorriente cuentaCorriente) {
            dto.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
            dto.setMargenAutorizado(cuentaCorriente.getMargenAutorizado());
            dto.setCostoMantenimiento(cuentaCorriente.getCostoMantenimiento());
        }

        return dto;
    }
}
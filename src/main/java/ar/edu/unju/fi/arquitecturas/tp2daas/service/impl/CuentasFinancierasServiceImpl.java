package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.CuentasFinancierasRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.CuentasFinancierasResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.TipoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CajaDeAhorro;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaCorriete;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentasFinancieras;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCuenta;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.CuentasFinancierasRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.CuentasFinancierasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentasFinancierasServiceImpl implements CuentasFinancierasService {

    private final CuentasFinancierasRepository cuentasFinancierasRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public CuentasFinancierasResponseDTO crearCuenta(CuentasFinancierasRequestDTO request) {
        log.info("Iniciando proceso de creación de cuenta con CBU: {}", request.getCbu());

        validarDatosCreacion(request);

        if (cuentasFinancierasRepository.existsByCbuOrAlias(request.getCbu(), request.getAlias())) {
            log.error("Fallo al crear cuenta. Ya existe un registro con CBU {} o Alias {}",
                    request.getCbu(), request.getAlias());
            throw new IllegalArgumentException("Ya existe una cuenta registrada con el mismo CBU o Alias.");
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado con el ID: " + request.getClienteId()));

        // Se instancia la subclase correspondiente; los parámetros no informados quedan en cero
        CuentasFinancieras cuenta = switch (request.getTipoCuenta()) {
            case CAJA_DE_AHORRO -> {
                CajaDeAhorro cajaDeAhorro = new CajaDeAhorro();
                cajaDeAhorro.setTasaInteres(request.getTasaInteres() != null ? request.getTasaInteres() : 0.0);
                cajaDeAhorro.setExtraccionesSinCosto(
                        request.getExtraccionesSinCosto() != null ? request.getExtraccionesSinCosto() : 0);
                yield cajaDeAhorro;
            }
            case CUENTA_CORRIENTE -> {
                CuentaCorriete cuentaCorriente = new CuentaCorriete();
                cuentaCorriente.setMargenAutorizado(
                        request.getMargenAutorizado() != null ? request.getMargenAutorizado() : 0.0);
                cuentaCorriente.setCostoMantenimiento(
                        request.getCostoMantenimiento() != null ? request.getCostoMantenimiento() : 0.0);
                yield cuentaCorriente;
            }
        };

        cuenta.setCBU(request.getCbu());
        cuenta.setAlias(request.getAlias());
        cuenta.setSaldo(request.getSaldoInicial());
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setCliente(cliente);

        CuentasFinancieras cuentaGuardada = cuentasFinancierasRepository.save(cuenta);

        log.info("Cuenta registrada exitosamente con ID: {}", cuentaGuardada.getId());
        return toResponse(cuentaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentasFinancierasResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando cuenta por ID: {}", id);
        return toResponse(buscarEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentasFinancierasResponseDTO obtenerPorCbu(String cbu) {
        log.debug("Buscando cuenta por CBU: {}", cbu);
        return cuentasFinancierasRepository.findByCbu(cbu)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el CBU: " + cbu));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentasFinancierasResponseDTO> listarTodas() {
        log.debug("Listando la totalidad de las cuentas registradas");
        return cuentasFinancierasRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentasFinancierasResponseDTO> listarPorCliente(Long clienteId) {
        log.debug("Listando las cuentas del cliente con ID: {}", clienteId);

        if (!clienteRepository.existsById(clienteId)) {
            throw new IllegalArgumentException("Cliente no encontrado con el ID: " + clienteId);
        }

        return cuentasFinancierasRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CuentasFinancierasResponseDTO actualizarCuenta(Long id, CuentasFinancierasRequestDTO request) {
        log.info("Iniciando actualización de datos para la cuenta con ID: {}", id);

        CuentasFinancieras cuentaExistente = buscarEntidadPorId(id);

        // Actualización selectiva de campos modificables del dominio
        if (request.getAlias() != null && !request.getAlias().equals(cuentaExistente.getAlias())) {
            validarAlias(request.getAlias());
            if (cuentasFinancierasRepository.existsByAliasAndIdNot(request.getAlias(), id)) {
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
        } else if (cuentaExistente instanceof CuentaCorriete cuentaCorriente) {
            if (request.getMargenAutorizado() != null) {
                cuentaCorriente.setMargenAutorizado(request.getMargenAutorizado());
            }
            if (request.getCostoMantenimiento() != null) {
                cuentaCorriente.setCostoMantenimiento(request.getCostoMantenimiento());
            }
        }

        return toResponse(cuentasFinancierasRepository.save(cuentaExistente));
    }

    @Override
    @Transactional
    public CuentasFinancierasResponseDTO cambiarEstado(Long id, EstadoCuenta nuevoEstado) {
        log.info("Solicitado el cambio de estado de la cuenta con ID {} a {}", id, nuevoEstado);

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de la cuenta es obligatorio.");
        }

        CuentasFinancieras cuenta = buscarEntidadPorId(id);
        cuenta.setEstado(nuevoEstado);

        return toResponse(cuentasFinancierasRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        log.info("Solicitada la eliminación de la cuenta con ID: {}", id);
        CuentasFinancieras cuenta = buscarEntidadPorId(id);
        cuentasFinancierasRepository.delete(cuenta);
        log.info("Cuenta con ID {} eliminada correctamente", id);
    }

    /*
       ------------------------------------------------------------------
       Métodos auxiliares
       ------------------------------------------------------------------
    */

    private CuentasFinancieras buscarEntidadPorId(Long id) {
        return cuentasFinancierasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el ID: " + id));
    }

    private void validarDatosCreacion(CuentasFinancierasRequestDTO request) {
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente titular es obligatorio.");
        }
        if (request.getTipoCuenta() == null) {
            throw new IllegalArgumentException("El tipo de cuenta es obligatorio.");
        }
        if (request.getCbu() == null || !request.getCbu().matches("\\d{22}")) {
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

    private void validarParametrosNoNegativos(CuentasFinancierasRequestDTO request) {
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

    private CuentasFinancierasResponseDTO toResponse(CuentasFinancieras cuenta) {
        CuentasFinancierasResponseDTO dto = CuentasFinancierasResponseDTO.builder()
                .id(cuenta.getId())
                .cbu(cuenta.getCBU())
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
        } else if (cuenta instanceof CuentaCorriete cuentaCorriente) {
            dto.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
            dto.setMargenAutorizado(cuentaCorriente.getMargenAutorizado());
            dto.setCostoMantenimiento(cuentaCorriente.getCostoMantenimiento());
        }

        return dto;
    }
}

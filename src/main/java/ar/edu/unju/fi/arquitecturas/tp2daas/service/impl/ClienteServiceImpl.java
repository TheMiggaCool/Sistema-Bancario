package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.ClienteRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.ClienteResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.exceptions.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.tp2daas.exceptions.RecursoYaExistenteException;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.CuentaBancaria;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.interfaces.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClienteResponseDTO crearCliente(ClienteRequestDTO request) {
        log.info("Iniciando proceso de creación de cliente con CUIL: {}", request.getCuil());

        if (clienteRepository.existsByCuilOrEmail(request.getCuil(), request.getEmail())) {
            log.error("Fallo al crear cliente. Ya existe un registro con CUIL {} o Email {}",
                    request.getCuil(), request.getEmail());
            throw new RecursoYaExistenteException("Ya existe un cliente registrado con el mismo CUIL o Email.");
        }

        Cliente titular = null;
        if (request.getTitularId() != null) {
            titular = clienteRepository.findById(request.getTitularId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el cliente titular con ID: " + request.getTitularId()));
        }

        Cliente cliente = Cliente.builder()
                .cuil(request.getCuil())
                .nombre(request.getNombre())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .titularidad(request.getTitularidad()) // "TITULAR" o "ADHERENTE"
                .titular(titular)
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente registrado exitosamente con ID: {}", clienteGuardado.getId());
        return toResponse(clienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando cliente por ID: {}", id);
        return clienteRepository.findByIdWithCuentas(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorCuil(String cuil) {
        log.debug("Buscando cliente por CUIL: {}", cuil);
        return clienteRepository.findByCuilWithCuentas(cuil)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el CUIL: " + cuil));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        log.debug("Listando la totalidad de los clientes registrados");
        return clienteRepository.findAllWithCuentas().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO request) {
        log.info("Iniciando actualización de datos para el cliente con ID: {}", id);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + id));

        // Validación de email único excluyendo al cliente actual
        if (request.getEmail() != null && !request.getEmail().equals(clienteExistente.getEmail())) {
            if (clienteRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new RecursoYaExistenteException("Ya existe otro cliente registrado con el email: " + request.getEmail());
            }
            clienteExistente.setEmail(request.getEmail());
        }

        clienteExistente.setNombre(request.getNombre());
        clienteExistente.setTelefono(request.getTelefono());
        clienteExistente.setDireccion(request.getDireccion());
        clienteExistente.setTitularidad(request.getTitularidad());

        return toResponse(clienteRepository.save(clienteExistente));
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        log.info("Solicitada la eliminación del cliente con ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con el ID: " + id));
        clienteRepository.delete(cliente);
        log.info("Cliente con ID {} eliminado correctamente", id);
    }

    private ClienteResponseDTO toResponse(Cliente cliente) {
        List<Long> cuentasIds = cliente.getCuentaBancaria() != null
                ? cliente.getCuentaBancaria().stream().map(CuentaBancaria::getId).toList()
                : Collections.emptyList();

        Long titularId = (cliente.getTitular() != null) ? cliente.getTitular().getId() : null;

        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .cuil(cliente.getCuil())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .titularidad(cliente.getTitularidad())
                .titularId(titularId)
                .cuentasIds(cuentasIds)
                .build();
    }
}
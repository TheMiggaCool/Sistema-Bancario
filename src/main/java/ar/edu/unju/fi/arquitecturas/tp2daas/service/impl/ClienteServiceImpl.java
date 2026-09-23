package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        log.info("Iniciando proceso de creación de cliente con CUIL: {}", cliente.getCuil());

        if (clienteRepository.existsByCuilOrEmail(cliente.getCuil(), cliente.getEmail())) {
            log.error("Fallo al crear cliente. Ya existe un registro con CUIL {} o Email {}",
                    cliente.getCuil(), cliente.getEmail());
            throw new IllegalArgumentException("Ya existe un cliente registrado con el mismo CUIL o Email.");
        }

        Cliente clienteGuardado = clienteRepository.save(cliente);

        log.info("Cliente registrado exitosamente con ID: {}", clienteGuardado.getId());
        return clienteGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        log.debug("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerPorCuil(String cuil) {
        log.debug("Buscando cliente por CUIL: {}", cuil);
        return clienteRepository.findByCuil(cuil)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con el CUIL: " + cuil));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.debug("Listando la totalidad de los clientes registrados");
        return clienteRepository.findAll();
    }

    @Override
    @Transactional
    public Cliente actualizarCliente(Long id, Cliente clienteDetalles) {
        log.info("Iniciando actualización de datos para el cliente con ID: {}", id);

        Cliente clienteExistente = obtenerPorId(id);

        // Actualización selectiva de campos modificables del dominio
        clienteExistente.setNombre(clienteDetalles.getNombre());
        clienteExistente.setEmail(clienteDetalles.getEmail());

        return clienteRepository.save(clienteExistente);
    }

    @Override
    @Transactional
    public void eliminarPorId(Long id) {
        log.info("Solicitada la eliminación del cliente con ID: {}", id);
        Cliente cliente = obtenerPorId(id);
        clienteRepository.delete(cliente);
        log.info("Cliente con ID {} eliminado correctamente", id);
    }
}

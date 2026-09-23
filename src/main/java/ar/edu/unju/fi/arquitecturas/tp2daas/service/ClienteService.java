package ar.edu.unju.fi.arquitecturas.tp2daas.service;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import java.util.List;

public interface ClienteService {

    Cliente crearCliente(Cliente cliente);

    Cliente obtenerPorId(Long id);

    Cliente obtenerPorCuil(String cuil);

    List<Cliente> listarTodos();

    Cliente actualizarCliente(Long id, Cliente clienteDetalles);

    void eliminarPorId(Long id);
}

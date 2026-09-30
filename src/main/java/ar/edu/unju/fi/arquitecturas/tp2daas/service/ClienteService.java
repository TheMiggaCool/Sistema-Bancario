package ar.edu.unju.fi.arquitecturas.tp2daas.service;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.ClienteRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO request);

    ClienteResponseDTO obtenerPorId(Long id);

    ClienteResponseDTO obtenerPorCuil(String cuil);

    List<ClienteResponseDTO> listarTodos();

    ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO request);

    void eliminarPorId(Long id);
}
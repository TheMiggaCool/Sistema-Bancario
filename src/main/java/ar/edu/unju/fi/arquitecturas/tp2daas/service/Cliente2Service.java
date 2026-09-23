package ar.edu.unju.fi.arquitecturas.tp2daas.service;

import ar.edu.unju.fi.arquitecturas.tp2daas.model.Cliente;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Cliente2Service {
    private final ClienteRepository clienteRepository;

    public Cliente crearCliente(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id){
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con el ID: " + id));
    }
}


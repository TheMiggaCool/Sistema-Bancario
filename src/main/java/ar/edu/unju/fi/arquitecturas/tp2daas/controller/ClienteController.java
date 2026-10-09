package ar.edu.unju.fi.arquitecturas.tp2daas.controller;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.ClienteRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.ClienteResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.interfaces.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteServiceImpl clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(@Valid @RequestBody ClienteRequestDTO request) {
        ClienteResponseDTO nuevoCliente = clienteService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    @GetMapping("/activar")
    public ResponseEntity<Map<String, String>> activarCliente(@RequestParam("token") String token) {
        clienteService.activarCliente(token);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Cuenta activada exitosamente. El cliente ahora está ACTIVO.");
        return ResponseEntity.ok(response);
    }
}
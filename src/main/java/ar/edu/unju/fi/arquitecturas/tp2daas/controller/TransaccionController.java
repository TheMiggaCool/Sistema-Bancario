package ar.edu.unju.fi.arquitecturas.tp2daas.controller;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.TransaccionRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.TransaccionResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.impl.TransaccionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionServiceImpl transaccionService;

    @PostMapping("/transferir")
    public ResponseEntity<TransaccionResponseDTO> transferir(@Valid @RequestBody TransaccionRequestDTO request) {
        TransaccionResponseDTO respuesta = transaccionService.registrarTransaccion(request);
        return ResponseEntity.ok(respuesta);
    }
}
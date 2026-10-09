package ar.edu.unju.fi.arquitecturas.tp2daas.controller;

import ar.edu.unju.fi.arquitecturas.tp2daas.dto.request.CuentaBancariaRequestDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.CuentaBancariaResponseDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.impl.CuentaBancariaServiceImpl;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.interfaces.CuentaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaBancariaController {

    private final CuentaBancariaServiceImpl cuentaBancariaService;

    @PostMapping
    public ResponseEntity<CuentaBancariaResponseDTO> crearCuenta(@Valid @RequestBody CuentaBancariaRequestDTO request) {
        CuentaBancariaResponseDTO nuevaCuenta = cuentaBancariaService.crearCuenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCuenta);
    }

    @GetMapping("/{cbu}")
    public ResponseEntity<CuentaBancariaResponseDTO> obtenerPorCbu(@PathVariable String cbu) {
        CuentaBancariaResponseDTO cuenta = cuentaBancariaService.obtenerPorCBU(cbu);
        return ResponseEntity.ok(cuenta);
    }
}
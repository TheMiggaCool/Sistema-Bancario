package ar.edu.unju.fi.arquitecturas.tp2daas.service.impl;

import ar.edu.unju.fi.arquitecturas.tp2daas.config.*;
import ar.edu.unju.fi.arquitecturas.tp2daas.exceptions.*;
import ar.edu.unju.fi.arquitecturas.tp2daas.model.*;
import ar.edu.unju.fi.arquitecturas.tp2daas.repository.*;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.*;
import ar.edu.unju.fi.arquitecturas.tp2daas.dto.response.ResumenLiquidacionDTO;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.interfaces.ComisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ComisionServiceImpl implements ComisionService {
    // Estados posibles de las cuentas
    private enum EstadoCobro {
        COBRADA, RECHAZADA, OMITIDA
    }


    private record ResultadoCobro(EstadoCobro estado, double monto) {
        static ResultadoCobro omitida() {
            return new ResultadoCobro(EstadoCobro.OMITIDA, 0.0);
        }
    }

    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final TransaccionRepository transaccionRepository;
    private final ComisionesProperties comisiones;
    private final TransactionTemplate transactionTemplate;

    public ComisionServiceImpl(CuentaBancariaRepository cuentaBancariaRepository,
                               TransaccionRepository transaccionRepository,
                               ComisionesProperties comisiones,
                               PlatformTransactionManager transactionManager) {
        this.cuentaBancariaRepository = cuentaBancariaRepository;
        this.transaccionRepository = transaccionRepository;
        this.comisiones = comisiones;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // Sin @Transactional a propósito: cada cuenta abre/cierra su propia transacción.
    @Override
    public ResumenLiquidacionDTO liquidarComisionesMensuales() {
        List<Long> cuentasIds = cuentaBancariaRepository.findIdsByEstado(EstadoCuenta.ACTIVA);
        log.info("Liquidación de comisiones: {} cuentas ACTIVAS a evaluar", cuentasIds.size());

        int cobradas = 0, rechazadas = 0, omitidas = 0, errores = 0;
        double total = 0.0;

        for (Long cuentaId : cuentasIds) {
            try {
                ResultadoCobro resultado = transactionTemplate.execute(status -> cobrarComision(cuentaId));
                switch (resultado.estado()) {
                    case COBRADA -> { cobradas++; total += resultado.monto(); }
                    case RECHAZADA -> rechazadas++;
                    case OMITIDA -> omitidas++;
                }
            } catch (Exception e) {
                errores++;
                log.error("Error al cobrar la comisión de la cuenta ID {}: {}", cuentaId, e.getMessage(), e);
            }
        }

        ResumenLiquidacionDTO resumen =
                new ResumenLiquidacionDTO(cuentasIds.size(), cobradas, rechazadas, omitidas, errores, total);
        log.info("Liquidación finalizada: {}", resumen);
        return resumen;
    }

    // Lógica principal del cobro de comisiones
    private ResultadoCobro cobrarComision(Long cuentaId) {
        // Evita pisar el saldo si hay operaciones concurrentes
        CuentaBancaria cuenta = cuentaBancariaRepository.findByIdForUpdate(cuentaId).orElse(null);

        // Se re-verifica el estado ya con el bloqueo tomado
        if (cuenta == null || cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            return ResultadoCobro.omitida();
        }

        double monto;
        double margen = 0.0;
        if (cuenta instanceof CuentaCorriente cuentaCorriente) {
            monto = comisiones.cuentaCorriente();
            margen = cuentaCorriente.getMargenAutorizado();
        } else if (cuenta instanceof CajaDeAhorro) {
            monto = comisiones.cajaAhorro();
        } else {
            log.warn("La cuenta ID {} no es Caja de Ahorro ni Cuenta Corriente; se omite", cuentaId);
            return ResultadoCobro.omitida();
        }

        if (monto <= 0) {
            return ResultadoCobro.omitida();
        }

        boolean hayFondos = monto <= cuenta.getSaldo() + margen;
        if (hayFondos) {
            cuenta.setSaldo(cuenta.getSaldo() - monto);
            cuentaBancariaRepository.save(cuenta);
        } else {
            log.warn("Fondos insuficientes para comisión en cuenta ID {}. Saldo: {}, margen: {}, comisión: {}",
                    cuentaId, cuenta.getSaldo(), margen, monto);
        }

        transaccionRepository.save(Transaccion.builder()
                .fecha(new Date())
                .monto(monto)
                .tipo(TipoTransaccion.DEBITO_COMISION)
                .estado(hayFondos ? EstadoTransaccion.COMPLETADA : EstadoTransaccion.RECHAZADA)
                .cuentaBancaria(cuenta)
                .build());

        return new ResultadoCobro(hayFondos ? EstadoCobro.COBRADA : EstadoCobro.RECHAZADA, monto);
    }
}

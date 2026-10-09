package ar.edu.unju.fi.arquitecturas.tp2daas.scheduler;
import ar.edu.unju.fi.arquitecturas.tp2daas.service.interfaces.ComisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiquidacionComisionesScheduler {
    private final ComisionService comisionService;

    // Programado para realizar la liquidación día 1 del mes, a horas 00:00
    @Scheduled(cron = "${app.comisiones.cron:0 0 0 1 * *}",
            zone = "${app.comisiones.zone:America/Argentina/Jujuy}")
    public void ejecutarLiquidacionMensual() {
        log.info("Iniciando liquidación mensual de comisiones");
        comisionService.liquidarComisionesMensuales();
    }

}

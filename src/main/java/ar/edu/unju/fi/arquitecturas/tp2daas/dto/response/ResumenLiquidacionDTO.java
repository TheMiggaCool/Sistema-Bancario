package ar.edu.unju.fi.arquitecturas.tp2daas.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenLiquidacionDTO {
    private int cuentasEvaluadas;
    private int cuentasCobradas;
    private int rechazadasPorSaldo;
    private int omitidas;
    private int conError;
    private double totalCobrado;
}

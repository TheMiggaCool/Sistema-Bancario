package ar.edu.unju.fi.arquitecturas.tp2daas.model;
import lombok.*;
import jakarta.persistence.*;

import java.util.Date;

@Table (name = "Transacciones")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Date fecha;

    @Column(nullable = false, precision = 15)
    private double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transaccion", nullable = false, length = 30)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_transaccion", nullable = false, length = 20)
    private EstadoTransaccion estado;

    // Relación transacciones n-1 cuenta
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuenta_bancaria_id", nullable = false)
    private CuentaBancaria cuentaBancaria;
}

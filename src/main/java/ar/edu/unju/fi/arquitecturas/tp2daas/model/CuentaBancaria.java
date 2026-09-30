package ar.edu.unju.fi.arquitecturas.tp2daas.model;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cuentas_bancarias")
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Definimos la estrategia de herencia
public class CuentaBancaria extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 22)
    private String CBU;

    @Column(nullable = false, unique = true, length = 50)
    private String alias;

    @Column(nullable = false, precision = 15)
    private double saldo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCuenta estado;

    // Relación cuentas n-1 cliente
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relación cuenta 1-n transacciones
    @OneToMany(
            mappedBy = "cuentaBancaria",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST
            },
            orphanRemoval = true
    )
    private List<Transaccion> transacciones = new ArrayList<>();;
}

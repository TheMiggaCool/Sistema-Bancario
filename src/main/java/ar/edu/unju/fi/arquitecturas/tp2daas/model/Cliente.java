package ar.edu.unju.fi.arquitecturas.tp2daas.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "clientes")
@Entity
public class Cliente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String cuil;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefono", nullable = false, length = 10)
    private String telefono;

    @Column(name = "dirección", nullable = false, length = 100)
    private String direccion;

    /**
     * Define la condición del cliente: "TITULAR" o "ADHERENTE" (cónyuge, hijo, etc.).
     */
    @Column(name = "titularidad", nullable = false, length = 100)
    private String titularidad;

    /**
     * Si este cliente es un ADHERENTE, referencia al cliente TITULAR de la cuenta.
     * Si este cliente es TITULAR, este campo permanece en null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id")
    private Cliente titular;

    /**
     * Lista de adherentes a cargo de este cliente (si es titular).
     */
    @OneToMany(
            mappedBy = "titular",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @Builder.Default
    private List<Cliente> adherentes = new ArrayList<>();

    // Relación cliente 1-n cuentas
    @OneToMany(
            mappedBy = "cliente",
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST
            },
            orphanRemoval = true
    )
    @Builder.Default
    private List<CuentaBancaria> cuentaBancaria = new ArrayList<>();
}
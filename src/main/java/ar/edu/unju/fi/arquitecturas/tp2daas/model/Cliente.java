package ar.edu.unju.fi.arquitecturas.tp2daas.model;
import ar.edu.unju.fi.arquitecturas.tp2daas.enums.EstadoCliente;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table (name = "clientes")

@Entity
public class Cliente extends AuditableEntity{

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

    @Column(name = "titularidad", nullable = false, length = 100)
    private String titularidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoCliente estado = EstadoCliente.PENDIENTE_ACTIVACION;

    @Column(name = "token_activacion", length = 36)
    private String tokenActivacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;

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
    private List<CuentaBancaria> cuentaBancaria  = new ArrayList<>();
}

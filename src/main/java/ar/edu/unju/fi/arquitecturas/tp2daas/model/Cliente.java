package ar.edu.unju.fi.arquitecturas.tp2daas.model;
import lombok.*;
import jakarta.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table (name = "clientes")

@Entity
public class Cliente {

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
    private List<CuentasFinancieras> cuentasFinancieras  = new ArrayList<>();
}

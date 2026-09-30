package ar.edu.unju.fi.arquitecturas.tp2daas.dto.response;

import lombok.*;

import java.util.List;

/**
 * DTO de salida con la información de un cliente.
 * <p>
 * Las cuentas del cliente se informan únicamente por su identificador; el detalle de
 * cada una se obtiene a través del servicio de cuentas bancarias.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    /**
     * Identificador único del cliente.
     */
    private Long id;

    /**
     * Clave Única de Identificación Laboral / Tributaria del cliente.
     */
    private String cuil;

    /**
     * Nombre completo del cliente.
     */
    private String nombre;

    /**
     * Dirección de correo electrónico del cliente.
     */
    private String email;

    /**
     * Teléfono de contacto del cliente.
     */
    private String telefono;

    /**
     * Domicilio del cliente.
     */
    private String direccion;

    /**
     * Titularidad del cliente.
     */
    private String titularidad;

    /**
     * Identificadores de las cuentas bancarias de las que el cliente es titular;
     * vacía si no posee ninguna.
     */
    private List<Long> cuentasIds;
}

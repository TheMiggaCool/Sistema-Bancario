package ar.edu.unju.fi.arquitecturas.tp2daas.dto.request;

import lombok.*;

/**
 * DTO de entrada para la creación y actualización de clientes.
 * <p>
 * En la creación se utilizan todos los campos. En la actualización se consideran
 * el nombre, el email, el teléfono, la dirección y la titularidad; el CUIL identifica
 * al cliente y no es modificable por esta vía.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDTO {

    /**
     * Clave Única de Identificación Laboral / Tributaria de 11 dígitos (solo en la creación).
     */
    private String cuil;

    /**
     * Nombre completo del cliente.
     */
    private String nombre;

    /**
     * Dirección de correo electrónico única del cliente.
     */
    private String email;

    /**
     * Teléfono de contacto del cliente (hasta 10 dígitos).
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
}

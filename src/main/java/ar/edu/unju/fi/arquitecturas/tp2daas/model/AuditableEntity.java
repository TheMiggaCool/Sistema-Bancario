package ar.edu.unju.fi.arquitecturas.tp2daas.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public abstract class AuditableEntity {

    /**
     * Fecha y hora en la que la entidad fue persistida por primera vez.
     * Gestionado automáticamente por Spring Data JPA.
     */
    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación realizada sobre la entidad.
     * Actualizado automáticamente en cada operación de actualización.
     */
    @LastModifiedDate
    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;
}

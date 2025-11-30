package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "archivo_evaluador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoEvaluador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluador_id", unique = true)
    @JsonBackReference
    private Evaluador evaluador;

    // Fotocopia de documento de identidad
    private String fotocopiaUrl;
    private String fotocopiaPublicId;

    // Certificados de estudios (pdf o zip)
    private String certificadosUrl;
    private String certificadosPublicId;

    // Certificado de cuenta bancaria
    private String cuentaBancariaUrl;
    private String cuentaBancariaPublicId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

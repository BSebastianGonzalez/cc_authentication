package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluador {
    @Id
    private Long id;
    private String nombre;
    private String apellido;
    private String afiliacionInstitucional;
    private String cvlac;
    private String googleScholar;
    private String orcid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    @JsonBackReference
    @MapsId
    private Account account;

    private NivelEstudios nivelEducativo;

    @ManyToMany
    @JoinTable(
            name = "evaluador_linea_investigacion",
            joinColumns = @JoinColumn(name = "evaluador_id"),
            inverseJoinColumns = @JoinColumn(name = "linea_investigacion_id")
    )
    @JsonManagedReference
    private List<LineaInvestigacion> lineasInvestigacionEvaluador = new ArrayList<>();

    @OneToOne(mappedBy = "evaluador", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference
    private ArchivoEvaluador archivoEvaluador;
}

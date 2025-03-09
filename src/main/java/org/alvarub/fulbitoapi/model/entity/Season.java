package org.alvarub.fulbitoapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@Entity
public class Season {

    //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // Ejemplo: ARG-2021
    private Long year;
    private String countrieName;

    @ManyToMany
    private List<Team> teams;
}
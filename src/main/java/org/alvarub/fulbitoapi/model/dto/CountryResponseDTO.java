package org.alvarub.fulbitoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class CountryResponseDTO implements Serializable {

    //
    @Schema(example = "1", description = "ID del pais")
    private Long id;

    @Schema(example = "Argentina", description = "Nombre del pais")
    private String name;

    @Schema(example = "https://url-del-logo", description = "URL de la bandera del pais")
    private String flag;

    @Schema(example = "AMERICA", description = "Continente al que pertenece el pais")
    private String continent;

    @Schema(example = "Conmebol")
    private String confederationName;


    @Schema(description = "Ligas asociadas al pais")
    private List<LeagueResponseDTO> leagues;

    @Schema(description = "Equipos asociados al pais")
    private List<TeamResponseDTO> teams;

}

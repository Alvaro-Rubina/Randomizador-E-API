package org.alvarub.fulbitoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class LeagueDTO implements Serializable {

    //
    @Schema(example = "Liga Profesional Argentina") @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Schema(example = "https://url-del-logo")
    @NotBlank(message = "El url del logo es obligatorio")
    private String logo;

    @Schema(example = "Argentina")
    @NotBlank(message = "El nombre del país es obligatorio")
    private String countrieName;

    @Schema(example = "[\"ARG-2024\", \"ARG-2023\"]") @NotEmpty(message = "La lista de temporadas no puede estar vacía")
    @Size(min = 1, message = "Debe haber al menos una temporada en la lista")
    private List<String> seasons; // El string hace referencia al "code" de la season
}

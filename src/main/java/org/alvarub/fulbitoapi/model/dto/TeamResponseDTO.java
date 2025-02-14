package org.alvarub.fulbitoapi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class TeamResponseDTO {

    @Schema(example = "1", description = "ID del equipo")
    private Long id;

    @Schema(example = "Boca Juniors", description = "Nombre del equipo")
    private String name;

    @Schema(example = "https://url-del-logo", description = "URL del logo (escudo) del equipo")
    private String logo;
}

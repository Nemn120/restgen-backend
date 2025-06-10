package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequestDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private String urlRepository;
    private Boolean isPrivate;

    @Valid
    @NotNull
    private PropertiesDTO properties;
}

package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;

import es.achavez.miw.tfm.restgen.generator.domain.ProjectProperties;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectRequestDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private ProjectStatus status;
    private String urlRepository;
    private String plantUmlDiagram;
    private Boolean isPrivate;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private String creationUser;
    private String updateUser;

    @Valid
    @NotNull
    private ProjectProperties properties;
}

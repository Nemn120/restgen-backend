package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;

import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FindAllProjectResponseDTO {

    private String id;
    private String name;
    private String description;
    private ProjectStatus status;
    private String urlRepository;
    private Boolean isPrivate;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private String creationUser;
    private String updateUser;

    private String basePath;
    private String port;
}

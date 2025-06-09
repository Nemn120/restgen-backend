package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    private String id;
    private String name;
    private String description;
    private ProjectStatus status;
    private String urlRepository;
    private String plantUmlDiagram;
    private Boolean isPrivate;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private String creationUser;
    private String updateUser;
    private ProjectProperties properties;

    private List<JavaClass> classes;

}

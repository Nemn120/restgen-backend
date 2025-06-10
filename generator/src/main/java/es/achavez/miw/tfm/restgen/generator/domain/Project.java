package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Project clone() {
        return Project.builder()
                .id(null)
                .name(this.name)
                .description(this.description)
                .status(this.status)
                .urlRepository(this.urlRepository)
                .plantUmlDiagram(this.plantUmlDiagram)
                .isPrivate(this.isPrivate)
                .creationDate(this.creationDate)
                .updateDate(this.updateDate)
                .creationUser(this.creationUser)
                .updateUser(this.updateUser)
                .properties(this.properties)
                .classes(this.classes != null ? new ArrayList<>(this.classes) : null)
                .build();
    }

}

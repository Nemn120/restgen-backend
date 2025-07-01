package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document;

import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectProperties;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "projects")
public class ProjectDocument {

    @Id
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

    private GithubRepository githubRepository;

    private List<JavaClass> classes;

}

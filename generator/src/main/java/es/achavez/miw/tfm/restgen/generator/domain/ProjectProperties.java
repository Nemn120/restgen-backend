package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectProperties {

    private ApplicationProperties application;
    private MavenProperties maven;
    private DocumentationProperties documentation;
    private SecurityProperties security;
    private DatabaseProperties database;

}

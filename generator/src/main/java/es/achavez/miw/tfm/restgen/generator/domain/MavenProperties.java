package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MavenProperties {

    private String groupId;
    private String artifactId;
    private String version;
    private String name;
    private String description;

}

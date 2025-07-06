package es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype;

import lombok.Data;

@Data
public class MavenPropertiesArchetype {

    private String groupId;
    private String artifactId;
    private String version;
    private String company;
    private String appName;
    private String basePath;
    private Integer port;
    private String secretKey;

    public MavenPropertiesArchetype(
            String groupId, String artifactId, String version,
            String appName, String basePath, Integer port, String secretKey) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.appName = appName;
        this.basePath = basePath;
        this.port = port;
        this.secretKey = secretKey;
    }
}

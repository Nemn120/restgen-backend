package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;

import es.achavez.miw.tfm.restgen.generator.domain.TypeDatabase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertiesDTO {

    @NotNull
    private Integer port;
    @NotBlank
    private String basePath;
    @NotBlank
    private String groupId;
    @NotBlank
    private String artifactId;
    @NotBlank
    private String mavenVersion;
    @NotBlank
    private String mavenName;

    private String mavenDescription;

    private String secretKey;

    private TypeDatabase databaseType;

    @Valid
    private DocumentationPropertiesDTO documentation;

}

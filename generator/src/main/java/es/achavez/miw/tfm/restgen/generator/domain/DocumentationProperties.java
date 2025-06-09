package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DocumentationProperties {

    private String title;
    private String description;
    private String version;
    private String termsOfServiceUrl;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String licenseName;
    private String licenseUrl;

    private String basePackage;
    private List<DocketField> docketFields = new ArrayList<>();
    private String className="SwaggerConfig.java";
    private String apiName;

    public DocumentationProperties() {
    }

    public DocumentationProperties(String title, String description, String version) {
        this.title = title;
        this.description = description;
        this.version = version;
    }
}

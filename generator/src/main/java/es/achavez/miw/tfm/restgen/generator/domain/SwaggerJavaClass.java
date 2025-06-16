package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SwaggerJavaClass {

    private String title;
    private String description;
    private String termsOfServiceUrl;
    private String contact;
    private String license;
    private String licenseUrl;
    private String version;
    private String emailContact;
    private String urlContact;

    private String apiName;
    private String basePackage;
    private List<DocketField> docketFields = new ArrayList<>();
    private String className="SwaggerConfig.java";
}

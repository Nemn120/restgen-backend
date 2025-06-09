package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocketField {

    private String name;
    private String apiName;
    private String basePackage;
    private String camelCaseName;
}

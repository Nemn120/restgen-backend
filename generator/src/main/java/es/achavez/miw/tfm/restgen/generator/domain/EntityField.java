package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityField {
    private String name;
    private TypeField type;
    private String description;
    private String defaultValue;
    private EntityRelation relation;
    private Integer minLength;
    private Integer maxLength;
    private Integer length;
    private Boolean unique;
    private String columnName;
    private Integer minimum;
    private Integer maximum;
    private Boolean required;

    private Boolean isEmail;
    private Boolean primaryKey;

}
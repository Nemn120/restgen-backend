package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnDefinition {

    private String name;
    private Integer length;
    private Integer precision;
    private Integer scale;
    private Boolean unique;
    private Boolean foreignkey;
    private Boolean nullable;
}

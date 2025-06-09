package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sequence {

    private Boolean create;
    private String name;
    private Integer increment;
}

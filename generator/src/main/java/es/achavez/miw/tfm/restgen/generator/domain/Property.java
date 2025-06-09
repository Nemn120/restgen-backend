package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Property {

    private String name;
    private String type;
    private VisibilityField visibility;
    private String relationNameWithId;

}

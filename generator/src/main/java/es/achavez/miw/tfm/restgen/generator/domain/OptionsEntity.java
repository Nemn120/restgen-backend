package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.InheritanceType;

@Getter
@Setter
public class OptionsEntity {

    private InheritanceType inheritanceStrategy;
    private String[] uniqueConstraints;
    private Discriminator discriminator;
    private Sequence sequence;
    private Boolean isAudited;


}

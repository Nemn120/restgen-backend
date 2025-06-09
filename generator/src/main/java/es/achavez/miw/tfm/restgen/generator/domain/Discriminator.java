package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Discriminator {

    private String value;
    private DiscriminatorColumn column;
    private DiscriminatorOptions options;
}

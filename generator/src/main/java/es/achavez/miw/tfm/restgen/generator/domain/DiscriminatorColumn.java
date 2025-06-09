package es.achavez.miw.tfm.restgen.generator.domain;


import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.DiscriminatorType;


@Getter
@Setter
public class DiscriminatorColumn {

    private String name;
    private DiscriminatorType type;
    private Integer length;
}

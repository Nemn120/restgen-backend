package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;

import es.achavez.miw.tfm.restgen.generator.domain.EntityClass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityDTO {

    private String name;
    private EntityClass entity;
}

package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;
import persistence.FetchType;

@Getter
@Setter
public class RelationColumn {
    private Relation type;
    private FetchType fetch;
    private String joinColumnReferenced;
    private Boolean notAudited;
}

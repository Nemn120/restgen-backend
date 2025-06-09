package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Column {

    private Property property;
    private Property propertyDTO;
    private ColumnDefinition column;
    private RelationColumn relation;

}

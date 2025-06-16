package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityClass {

    private String extendsClass;
    private String tableName;
    private OptionsEntity options;
    private List<Column> columns;
}

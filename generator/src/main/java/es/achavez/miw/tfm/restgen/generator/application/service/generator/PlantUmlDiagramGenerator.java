package es.achavez.miw.tfm.restgen.generator.application.service.generator;

import es.achavez.miw.tfm.restgen.generator.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlantUmlDiagramGenerator {

    private StringBuilder plantUml;

    public PlantUmlDiagramGenerator() {
        this.plantUml = new StringBuilder("!theme cerulean\n\n");
    }

    public String generateDiagram(List<JavaClass> entities) {
        if (entities == null || entities.isEmpty() ||
            entities.stream()
                    .allMatch(entity -> entity.getEntity() == null || entity.getEntity().getColumns().isEmpty())) {
            return null;
        }
        for (JavaClass entity : entities) {
            plantUml.append(generateClassDiagram(entity)).append("\n\n");
        }

        for (JavaClass entity : entities) {
            plantUml.append(generateRelations(entity));
        }

        String plantUmlString = plantUml.toString();
        this.plantUml =new StringBuilder("!theme cerulean\n\n");
        return plantUmlString;
    }

    private String generateClassDiagram(JavaClass entity) {
        StringBuilder diagram = new StringBuilder("class ").append(entity.getName());
        if (StringUtils.isNotBlank(entity.getEntity().getExtendsClass())
            && !entity.getEntity().getExtendsClass().equals(AnnotationPersistence.AUDITABLE_ENTITY.name())) {
            diagram.append(" extends ").append(entity.getEntity().getExtendsClass());
        }
        diagram.append(" {\n");
        for (Column field : entity.getEntity().getColumns()) {
            diagram.append("  ").append(generateFieldDiagram(field)).append("\n");
        }
        diagram.append("}");
        return diagram.toString();
    }

    private String generateFieldDiagram(Column field) {
        if(Boolean.TRUE.equals(field.getColumn().getForeignkey())){
            return field.getProperty().getName() + " : " + field.getProperty().getType();
        }else{
            DataTypes dataTypes = DataTypes.valueOf(field.getProperty().getType());
            return field.getProperty().getName() + " : " + dataTypes.getName();
        }
    }

    private String generateRelations(JavaClass entity) {
        StringBuilder relations = new StringBuilder();
        for (Column field : entity.getEntity().getColumns()) {
            RelationColumn relation = field.getRelation();
            if (Boolean.TRUE.equals(field.getColumn().getForeignkey()) &&
                relation != null && isValidRelationType(relation)) {
                switch (relation.getType()) {
                    case Relation.ONE_TO_ONE -> relations.append(entity.getName())
                            .append(" -down-> ").append(field.getProperty().getType()).append("\n");
                    case Relation.MANY_TO_ONE -> relations.append(entity.getName())
                            .append(" -down-> \"0..*\" ").append(field.getProperty().getType()).append("\n");
                }
            }
        }
        return relations.toString();
    }

    private boolean isValidRelationType(RelationColumn relation) {
        return relation.getType() != null && relation.getType().getName() != null;
    }
}
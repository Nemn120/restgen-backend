package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.GeneratorUtil;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.*;
import jakarta.persistence.FetchType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.List;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.*;


public class EntityClassGenerator<T extends JavaClassSource> extends JavaClassTemplate<T> {

    public static final String NULLABLE = "nullable";
    public static final String REFERENCED_COLUMN_NAME = "referencedColumnName";
    private static Logger logger = LogManager.getLogger(EntityClassGenerator.class);
    private EntityClass entityClass;
    private OptionsEntity options;

    public EntityClassGenerator(T javaClassSource, JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        super(javaClassSource, javaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.ENTITY;
    }

    @Override
    public void decorate() {
        super.decorate();
        getJavaClassSource().setName(javaClass.getName());
        entityClass = javaClass.getEntity();
        addAnnotationAndImport(ENTITY);
        addTableAnnotation();
        addExtendsAuditableEntity();
        options = entityClass.getOptions();
        if(options != null){
            addInheritanceAnnotation();
            addDiscriminator();
            addSequenceGeneratorAnnotation();
        }
        addGetterAndSetter();
        addColumns();
    }

    private void addGetterAndSetter() {
        addAnnotationAndImport(LOMBOK_GETTER);
        addAnnotationAndImport(LOMBOK_SETTER);
    }

    private void addExtendsAuditableEntity() {
        EntityClass entityClass = javaClass.getEntity();
        if (StringUtils.isBlank(entityClass.getExtendsClass()) || AUDITABLE_ENTITY.name().equals(entityClass.getExtendsClass())) {
            addExtendsWithoutPackage(AUDITABLE_ENTITY);
            logger.info("addExtendsAuditableEntity(): Generando extension de " + ABSTRACT_ID_ENTITY.getAnnotationName());
        }else {
            getJavaClassSource().setSuperType(entityClass.getExtendsClass());
        }
    }

    protected void addExtendsWithoutPackage(AnnotationPersistence abstractIdAuditableEntity) {
        getJavaClassSource().setSuperType(abstractIdAuditableEntity.getAnnotationName());
    }

    private void addInheritanceAnnotation() {
        if(options.getInheritanceStrategy() != null){
            AnnotationSource<JavaClassSource> annotationSource = addAnnotationAndImport(INHERITANCE);
            annotationSource.setEnumValue("strategy", options.getInheritanceStrategy());
        }
    }

    private void addDiscriminator() {
        Discriminator discriminator = options.getDiscriminator();
        if(discriminator != null){
            addDiscriminatorAnnotations(discriminator);
            addDiscriminatorValue(discriminator);
        }
    }

    private void addSequenceGeneratorAnnotation() {
        logger.info("addSecuenceGeneratorAnnotation(): Generando Secuencia");
        Sequence sequence = options.getSequence();
        if(sequence != null){
            if (Boolean.TRUE.equals(sequence.getCreate())) {
                AnnotationSource<JavaClassSource> annotation = addAnnotationAndImport(SEQUENCE_GENERATOR);
                annotation.setStringValue("name", "generator");
                if (sequence.getName() != null) {
                    annotation.setStringValue("sequenceName", sequence.getName());
                } else {
                    annotation.setStringValue("sequenceName", entityClass.getTableName() + "_SEQ");
                }
                if (sequence.getIncrement() != null) {
                    annotation.setLiteralValue("allocationSize", String.valueOf(sequence.getIncrement()));
                } else {
                    annotation.setLiteralValue("allocationSize", "1");
                }
            }
        }
    }

    private void addDiscriminatorAnnotations(Discriminator discriminator) {
        addDiscriminatorColumnAnnotation(discriminator);
        addDiscriminatorOptionsAnnotation(discriminator);
    }

    private void addDiscriminatorValue(Discriminator discriminator) {
        if (StringUtils.isNotBlank(discriminator.getValue())) {
            AnnotationSource<JavaClassSource> addAnnotation = addAnnotationAndImport(DISCRIMINATOR_VALUE);
            addAnnotation.setStringValue(discriminator.getValue());
        }
    }

    private void addDiscriminatorColumnAnnotation(Discriminator discriminator) {
        DiscriminatorColumn column = discriminator.getColumn();
        if(column != null && StringUtils.isNotBlank(column.getName())) {
            AnnotationSource<JavaClassSource> addAnnotation = addAnnotationAndImport(DISCRIMINATOR_COLUMN);

            if (StringUtils.isNotBlank(column.getName())) {
                addAnnotation.setStringValue("name", column.getName());
            }
            if (column.getType() != null) {
                addAnnotation.setEnumValue("discriminatorType", column.getType());
            }
            if (column.getLength() != null) {
                addAnnotation.setLiteralValue("length", String.valueOf(column.getLength()));
            }
        }
    }

    private void addDiscriminatorOptionsAnnotation(Discriminator discriminator) {
        DiscriminatorOptions options = discriminator.getOptions();
        if(options != null){
            if (Boolean.TRUE.equals(options.getForce()) ||
                    Boolean.FALSE.equals(options.getInsert())) {
                AnnotationSource<JavaClassSource> annotationDiscriminatorOptions = addAnnotationAndImport(DISCRIMINATOR_OPTIONS);
                if (Boolean.TRUE.equals(options.getForce())) {
                    annotationDiscriminatorOptions.setLiteralValue("force", String.valueOf(true));
                }
                if (Boolean.FALSE.equals(options.getInsert())) {
                    annotationDiscriminatorOptions.setLiteralValue("insert", String.valueOf(false));
                }
            }
        }
    }

    private void addTableAnnotation() {
        logger.info("addTableAnnotation(): Generando Anotaciones a nivel de tabla");
        if(entityClass == null){
            logger.warn("addTableAnnotation(): entityClass is null, cannot add table annotation");
            return;
        }
        if (StringUtils.isBlank(entityClass.getTableName())) {
            entityClass.setTableName(javaClass.getName().toUpperCase());
        }
        AnnotationSource<JavaClassSource> annotation = addAnnotationAndImport(TABLE);
        annotation.setStringValue("name", entityClass.getTableName());
        OptionsEntity options1 = entityClass.getOptions();

        if (options1.getUniqueConstraints() != null && options1.getUniqueConstraints().length > 0) {
            addImport(UNIQUE_CONSTRAINT);
            annotation.setLiteralValue("uniqueConstraints", "@" + UNIQUE_CONSTRAINT.getAnnotationName() + "(columnNames = {\"" + String.join("\", \"", options1.getUniqueConstraints()) + "\"})");
        }
    }

    private void addColumns() {
        logger.info("addColumns(): Generando columnas en la entidad");
        EntityClass entityClass = javaClass.getEntity();

        if (entityClass.getColumns() != null) {
            generateColumnFields(entityClass.getColumns());
        }
    }

    private void generateColumnFields(List<Column> columns) {
        for (Column column : columns) {
            FieldSource<JavaClassSource> fieldSource = javaClassSource.addField();
            Property property = column.getProperty();
            setPropertyVisibility(property, fieldSource);
            fieldSource.setName(property.getName());

            ColumnDefinition columnDefinition = column.getColumn();
            if (Boolean.TRUE.equals(columnDefinition.getForeignkey())) {
                addForeignKeyField(fieldSource, column);
            } else {
                addSimpleField(fieldSource, property, column);
            }
        }
    }

    private void setPropertyVisibility(Property property, FieldSource<JavaClassSource> fieldSource) {
        VisibilityField visibility = property.getVisibility() != null ? property.getVisibility() : VisibilityField.PRIVATE;
        fieldSource.setVisibility(Visibility.valueOf(visibility.name()));
    }

    private void addForeignKeyField(FieldSource<JavaClassSource> fieldSource, Column column) {
        RelationColumn relation = column.getRelation();
        Validate.notNull(relation, "Relation cannot be null");

        fieldSource.setType(column.getProperty().getType());

        switch (relation.getType()) {
            case MANY_TO_ONE -> addManyToOneRelation(fieldSource, relation);
            case ONE_TO_ONE -> addOneToOneRelation(fieldSource, relation);
            default -> throw new IllegalArgumentException("Unsupported relation type: " + relation.getType());
        }
        addJoinColumnAnnotation(fieldSource, column);
    }

    private void addManyToOneRelation(FieldSource<JavaClassSource> fieldSource, RelationColumn relation) {
        AnnotationSource<JavaClassSource> annotationRelation = fieldSource.addAnnotation(relation.getType().getName());
        javaClassSource.addImport(relation.getType().getPackageImport());

        addFetchTypeLazy(relation, annotationRelation);
    }

    private void addOneToOneRelation(FieldSource<JavaClassSource> fieldSource, RelationColumn relation) {
        AnnotationSource<JavaClassSource> annotationRelation = fieldSource.addAnnotation(relation.getType().getName());
        javaClassSource.addImport(relation.getType().getPackageImport());

        addFetchTypeLazy(relation, annotationRelation);
    }

    private void addFetchTypeLazy(RelationColumn relation, AnnotationSource<JavaClassSource> annotationRelation) {
        if (FetchType.LAZY.equals(relation.getFetch())) {
            annotationRelation.setEnumValue("fetch", relation.getFetch());
        }
    }

    private void addJoinColumnAnnotation(FieldSource<JavaClassSource> fieldSource, Column column) {
        AnnotationSource<JavaClassSource> joinColumnAnnotation = fieldSource.addAnnotation(JOIN_COLUMN.getAnnotationName());
        addImport(JOIN_COLUMN);

        setJoinColumnName(joinColumnAnnotation, column);
        setJoinColumnReferencedColumnName(joinColumnAnnotation, column);
        setJoinColumnNullable(joinColumnAnnotation, column.getColumn());
    }

    private void addSimpleField(FieldSource<JavaClassSource> fieldSource, Property property, Column column) {
        DataTypes dataTypes = DataTypes.valueOf(property.getType());
        if(dataTypes.getImportPath() != null){
            javaClassSource.addImport(dataTypes.getImportPath());
        }

        fieldSource.setType(dataTypes.getName());
        javaClassSource.addImport(COLUMN.getPackageName());
        addColumnAnnotation(fieldSource, column);
    }

    private void addColumnAnnotation(FieldSource<JavaClassSource> fieldSource, Column column) {
        AnnotationSource<JavaClassSource> annotation = fieldSource.addAnnotation(COLUMN.getPackageName());
        ColumnDefinition columnDefinition = column.getColumn();
        setColumnName(annotation, column);
        setColumnUnique(annotation, columnDefinition);
        setColumnPrecision(annotation, columnDefinition);
        setColumnScale(annotation, columnDefinition);
    }

    private void setColumnName(AnnotationSource<JavaClassSource> annotation, Column column) {
        ColumnDefinition columnDefinition = column.getColumn();
        if (StringUtils.isNotBlank(columnDefinition.getName())) {
            annotation.setStringValue("name", columnDefinition.getName());
        } else {
            annotation.setStringValue("name", "ID_" + GeneratorUtil.convertCamelToSnakeCaseUpper(column.getProperty().getName()));
        }
    }

    private void setColumnUnique(AnnotationSource<JavaClassSource> annotation, ColumnDefinition columnDefinition) {
        if (Boolean.TRUE.equals(columnDefinition.getUnique())) {
            annotation.setLiteralValue("unique", String.valueOf(columnDefinition.getUnique()));
        }
    }

    private void setColumnPrecision(AnnotationSource<JavaClassSource> annotation, ColumnDefinition columnDefinition) {
        if (columnDefinition.getPrecision() != null) {
            annotation.setLiteralValue("precision", String.valueOf(columnDefinition.getPrecision()));
        }
    }

    private void setColumnScale(AnnotationSource<JavaClassSource> annotation, ColumnDefinition columnDefinition) {
        if (columnDefinition.getScale() != null) {
            annotation.setLiteralValue("scale", String.valueOf(columnDefinition.getScale()));
        }
    }

    private void setJoinColumnName(AnnotationSource<JavaClassSource> joinColumnAnnotation, Column column) {
        if (StringUtils.isNotBlank(column.getColumn().getName())) {
            joinColumnAnnotation.setStringValue("name", column.getColumn().getName());
        } else {
            joinColumnAnnotation.setStringValue("name", "ID_" + GeneratorUtil.convertCamelToSnakeCaseUpper(column.getProperty().getName()));
        }
    }

    private void setJoinColumnReferencedColumnName(AnnotationSource<JavaClassSource> joinColumnAnnotation, Column column) {
        RelationColumn relation = column.getRelation();
        if (StringUtils.isNotBlank(relation.getJoinColumnReferenced())) {
            joinColumnAnnotation.setStringValue(REFERENCED_COLUMN_NAME, relation.getJoinColumnReferenced());
        } else {
            joinColumnAnnotation.setStringValue(REFERENCED_COLUMN_NAME, "ID");
        }
    }

    private void setJoinColumnNullable(AnnotationSource<JavaClassSource> joinColumnAnnotation, ColumnDefinition columnDefinition) {
        if (Boolean.FALSE.equals(columnDefinition.getNullable())) {
            joinColumnAnnotation.setLiteralValue(NULLABLE, String.valueOf(Boolean.FALSE));
        }
    }
}

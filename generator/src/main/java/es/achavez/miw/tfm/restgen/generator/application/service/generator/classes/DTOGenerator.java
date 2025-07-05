package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.GeneratorUtil;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.*;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.AUDITABLE_ENTITY;

public class DTOGenerator<T extends JavaClassSource> extends JavaClassTemplate<T> {

    public DTOGenerator(T javaClassSource, JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        super(javaClassSource, javaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.DTO;
    }

    @Override
    public void decorate() {
        super.decorate();

        String dtoClassName = packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO);
        getJavaClassSource().setName(dtoClassName);

        addAnnotationAndImport(AnnotationPersistence.LOMBOK_GETTER);
        addAnnotationAndImport(AnnotationPersistence.LOMBOK_SETTER);

        addExtendsAuditableEntity();

        for (Column column : javaClass.getEntity().getColumns()) {
            if (column.getPropertyDTO() != null) {
                getJavaClassSource().addField()
                        .setName(column.getPropertyDTO().getName())
                        .setType(column.getPropertyDTO().getType())
                        .setPrivate();
            } else {
                DataTypes dataTypes = DataTypes.valueOf(column.getProperty().getType());
                if(dataTypes.getImportPath() != null){
                    javaClassSource.addImport(dataTypes.getImportPath());
                }
                getJavaClassSource().addField()
                        .setName(column.getProperty().getName())
                        .setType(dataTypes.getName())
                        .setPrivate();
            }
        }
    }

    private void addExtendsAuditableEntity() {
        EntityClass entityClass = javaClass.getEntity();
        if(entityClass.getExtendsClass() != null && !AUDITABLE_ENTITY.name().equals(entityClass.getExtendsClass())){
            String DTOExtends = GeneratorUtil.snakeCaseToUpperCamelCase(entityClass.getExtendsClass()).concat("DTO");
            getJavaClassSource().setSuperType(DTOExtends);
        }else{
            addExtendsWithoutPackage(AnnotationPersistence.AUDITABLE_DTO);
        }
    }

    protected void addExtendsWithoutPackage(AnnotationPersistence abstractIdAuditableEntity) {
        getJavaClassSource().setSuperType(abstractIdAuditableEntity.getAnnotationName()+ "<" + DataTypes.LONG.getName() + ">");
    }
}
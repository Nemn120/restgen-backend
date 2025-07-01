package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.DataTypes;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.AUTOWIRED;


public class ServiceImplClassGenerator<T extends JavaClassSource> extends JavaClassTemplate<T> {

    public static final String BEAN_NAME = ".BEAN_NAME";

    public ServiceImplClassGenerator(T javaClassSource, JavaClass javaClassDTO, MavenProjectPath mavenProjectPath) {
        super(javaClassSource, javaClassDTO, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.SERVICE_IMPL;
    }

    @Override
    public void decorate() {
        super.decorate();
        String serviceName = packageDirectory.getNameClassLayer(DirectoryLayerPath.SERVICE);
        String serviceImplName = packageDirectory.getNameClassLayer(DirectoryLayerPath.SERVICE_IMPL);
        getJavaClassSource().setName(serviceImplName);
        String serviceBeanName = serviceName.concat(BEAN_NAME);

        addImport(DirectoryLayerPath.DTO);
        addImport(DirectoryLayerPath.ENTITY);
        addImport(DirectoryLayerPath.MAPPER);
        addImport(DirectoryLayerPath.REPOSITORY);
        addImport(DirectoryLayerPath.SERVICE);
        addSubPackageImport(AnnotationPersistence.GENERIC_REPOSITORY);
        addSubPackageImport(AnnotationPersistence.GENERIC_MAPPER);

        AnnotationSource<JavaClassSource> annotationService = addAnnotationAndImport(AnnotationPersistence.SERVICE);
        annotationService.setLiteralValue(serviceBeanName);

        addImport(AUTOWIRED);
        addAnnotationAndImport(AnnotationPersistence.TRANSACTIONAL);

        StringBuilder extendsGestionGenericService = new StringBuilder(AnnotationPersistence.GENERIC_SERVICE_IMPL.getAnnotationName());
        extendsGestionGenericService.append("<")
                .append(packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY))
                .append(", ")
                .append(packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO))
                .append(", ")
                .append(DataTypes.LONG.getName())
                .append(">");
        getJavaClassSource().setSuperType(extendsGestionGenericService.toString());
        getJavaClassSource().addInterface(packageDirectory.getNameClassLayer(DirectoryLayerPath.SERVICE));

        FieldSource<JavaClassSource> repoField = getJavaClassSource().addField();
        repoField.setName("repo");
        repoField.setType(packageDirectory.getNameClassLayer(DirectoryLayerPath.REPOSITORY));
        repoField.setVisibility(Visibility.PRIVATE);
        repoField.addAnnotation(AnnotationPersistence.AUTOWIRED.getAnnotationName());

        FieldSource<JavaClassSource> mapperField = getJavaClassSource().addField();
        mapperField.setName("mapper");
        mapperField.setType(packageDirectory.getNameClassLayer(DirectoryLayerPath.MAPPER));
        mapperField.setVisibility(Visibility.PRIVATE);
        mapperField.addAnnotation(AnnotationPersistence.AUTOWIRED.getAnnotationName());

        MethodSource<JavaClassSource> getRepoMethod = getJavaClassSource().addMethod();
        getRepoMethod.setName("getRepo");
        getRepoMethod.setReturnType("GenericRepository<" +
                                    packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY) + ", "+DataTypes.LONG.getName()+">");
        getRepoMethod.setVisibility(Visibility.PROTECTED);
        getRepoMethod.addAnnotation(Override.class);
        getRepoMethod.setBody("return repo;");

        MethodSource<JavaClassSource> getMapperMethod = getJavaClassSource().addMethod();
        getMapperMethod.setName("getMapper");
        getMapperMethod.setReturnType("GenericMapper<" +
                                      packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY) + ", " +
                                      packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO) + ">");
        getMapperMethod.setVisibility(Visibility.PROTECTED);
        getMapperMethod.addAnnotation(Override.class);
        getMapperMethod.setBody("return mapper;");

        MethodSource<JavaClassSource> extractIdMethod = getJavaClassSource().addMethod();
        extractIdMethod.setName("extractIdFromDto");
        extractIdMethod.setReturnType(DataTypes.LONG.getName());
        extractIdMethod.setVisibility(Visibility.PROTECTED);
        extractIdMethod.addAnnotation(Override.class);
        extractIdMethod.addParameter(packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO), "dto");
        extractIdMethod.setBody("return dto.getId();");
    }
}

package es.achavez.miw.tfm.restgen.generator.application.service.generator;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.classes.*;
import es.achavez.miw.tfm.restgen.generator.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeneratedJavaClass {

    private static Logger logger = LogManager.getLogger(GeneratedJavaClass.class);

    public void generate(Project project, MavenProjectPath mavenProjectPath) {
        List<DocketField> docketFields = new ArrayList<>();

        List<JavaClass> genericJavaClasses = new ArrayList<>();
        for (JavaClass javaClass : project.getClasses()) {
            JavaClass javaClassGenerate = generateJavaFiles(javaClass, mavenProjectPath);
            if (javaClassGenerate != null) {
                DocketField docketField = DocketField.builder()
                        .apiName(javaClassGenerate.getApiName())
                        .name(GeneratorUtil.convertCamelToSnakeCaseLower(javaClassGenerate.getName()))
                        .camelCaseName(GeneratorUtil.convertUpperFisrtLettersAndCamelCase(javaClassGenerate.getName()))
                        .build();
                docketFields.add(docketField);
                genericJavaClasses.add(javaClassGenerate);
            }
        }
        generateDTOAndMapperFile(genericJavaClasses, mavenProjectPath);
        SwaggerJavaClass documentationSwagger = getSwaggerDocumentationJavaClass(project.getProperties(), docketFields);
        generateSwaggerConfig(documentationSwagger, mavenProjectPath);
    }

    private SwaggerJavaClass getSwaggerDocumentationJavaClass(ProjectProperties properties, List<DocketField> docketFields) {
        DocumentationProperties documentation = properties.getDocumentation();
        if (documentation == null) {
            logger.warn("No documentation properties found, returning empty SwaggerJavaClass");
            return new SwaggerJavaClass();
        }
        SwaggerJavaClass documentationSwagger = new SwaggerJavaClass();
        documentationSwagger.setDescription(documentation.getDescription());
        documentationSwagger.setTitle(documentation.getTitle());
        documentationSwagger.setVersion(documentation.getVersion());
        documentationSwagger.setApiName(properties.getApplication().getBasePath());
        documentationSwagger.setClassName(documentation.getClassName());
        documentationSwagger.setEmailContact(documentation.getContactEmail());
        documentationSwagger.setLicense(documentation.getLicenseName());
        documentationSwagger.setLicenseUrl(documentation.getLicenseUrl());
        documentationSwagger.setTermsOfServiceUrl(documentation.getTermsOfServiceUrl());
        documentationSwagger.setDocketFields(docketFields);

        //TODO por revisar si falta agregar mas
        return documentationSwagger;
    }

    private JavaClass generateJavaFiles(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        if (null != javaClass) {
            generateEntity(javaClass, mavenProjectPath);
            generateService(javaClass, mavenProjectPath);
            generateServiceImpl(javaClass, mavenProjectPath);
            generateRepository(javaClass, mavenProjectPath);
            generateController(javaClass, mavenProjectPath);
            generateControllerImpl(javaClass, mavenProjectPath);
        }
        return javaClass;
    }


    private void generateDTOAndMapperFile(List<JavaClass> genericJavaClasses, MavenProjectPath mavenProjectPath) {
        for (JavaClass genericJavaClass : genericJavaClasses) {
            for (Column column : genericJavaClass.getEntity().getColumns()) {
                if(column.getRelation() != null && column.getRelation().getType() != null) {
                    String name = column.getProperty().getName();
                    String dtoRelationName= GeneratorUtil.concatRelationAndKeyRelation(name, "id");
                    if(column.getPropertyDTO() ==  null){
                        column.setPropertyDTO(new Property());
                    }
                    column.getPropertyDTO().setName(dtoRelationName);
                    String entityRelationNameWithPrimaryKey= column.getProperty().getName().concat(".").concat("id");
                    column.getPropertyDTO().setRelationNameWithId(entityRelationNameWithPrimaryKey);
                    column.getPropertyDTO().setType(DataTypes.LONG.getName());
                }
            }
            generateDTO(genericJavaClass, mavenProjectPath);
            generateMapper(genericJavaClass, mavenProjectPath);
        }
    }

    private void generateDTO(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate Service");
        final JavaClassSource dto = Roaster.create(JavaClassSource.class);
        DTODecorator serviceClassDecorator = new DTODecorator(dto, javaClass, mavenProjectPath);
        serviceClassDecorator.decorate();
        generateFile(serviceClassDecorator.getJavaClassSource(), mavenProjectPath.getDTOMainPath());
    }

    private void generateMapper(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate Mapper");
        final JavaInterfaceSource mapper = Roaster.create(JavaInterfaceSource.class);
        MapperDecorator mapperDecorator = new MapperDecorator(mapper, javaClass, mavenProjectPath);
        mapperDecorator.decorate();
        generateFile(mapperDecorator.getJavaClassSource(), mavenProjectPath.getMapperMainPath());
    }

    private void generateSwaggerConfig(SwaggerJavaClass docProperties, MavenProjectPath mavenProjectPath) {
        if (docProperties != null) {
            final JavaClassSource swaggerClass = Roaster.create(JavaClassSource.class);
            SwaggerDecorator swaggerDecorator = new SwaggerDecorator(swaggerClass, docProperties, mavenProjectPath);
            swaggerDecorator.decorate();
            generateFile(swaggerDecorator.getJavaClassSource(), mavenProjectPath.getConfigMainPath());
        }
    }


    private void generateRepository(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate Repository");

        final JavaInterfaceSource javaClassRepository = Roaster.create(JavaInterfaceSource.class);
        JavaClassDecorator repositoryDecorator = new RepositoryClassDecorator(javaClassRepository, javaClass, mavenProjectPath);
        repositoryDecorator.decorate();
        print(repositoryDecorator);
        generateFile(repositoryDecorator.getJavaClassSource(), mavenProjectPath.getRepositoryMainPath());
    }

    private void generateEntity(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate Entity");
        final JavaClassSource javaClassEntity = Roaster.create(JavaClassSource.class);
        JavaClassDecorator entityDecorator = new EntityClassDecorator(javaClassEntity, javaClass, mavenProjectPath);
        entityDecorator.decorate();
        print(entityDecorator);
        generateFile(entityDecorator.getJavaClassSource(), mavenProjectPath.getEntityMainPath());
    }

    private void generateService(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate Service");
        final JavaInterfaceSource service = Roaster.create(JavaInterfaceSource.class);
        JavaClassDecorator serviceClassDecorator = new ServiceClassDecorator(service, javaClass, mavenProjectPath);
        serviceClassDecorator.decorate();
        generateFile(serviceClassDecorator.getJavaClassSource(), mavenProjectPath.getServiceMainPath());
    }

    private void generateServiceImpl(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate ServiceImpl");
        final JavaClassSource javaClassEntity = Roaster.create(JavaClassSource.class);
        JavaClassDecorator entityDecorator = new ServiceImplClassDecorator(javaClassEntity, javaClass, mavenProjectPath);
        entityDecorator.decorate();
        generateFile(entityDecorator.getJavaClassSource(), mavenProjectPath.getServiceImplMainPath());
    }

    private void generateController(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate Controller");
        final JavaInterfaceSource javaInterfaceSource = Roaster.create(JavaInterfaceSource.class);
        JavaClassDecorator controllerClassDecorator = new ControllerClassDecorator(javaInterfaceSource, javaClass, mavenProjectPath);
        controllerClassDecorator.decorate();
        generateFile(controllerClassDecorator.getJavaClassSource(), mavenProjectPath.getControllerMainPath());
    }

    private void generateControllerImpl(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        logger.info("Generate ControllerImpl");
        final JavaClassSource javaClassSource = Roaster.create(JavaClassSource.class);
        JavaClassDecorator classDecorator = new ControllerImplClassDecorator(javaClassSource, javaClass, mavenProjectPath);
        classDecorator.decorate();
        generateFile(classDecorator.getJavaClassSource(), mavenProjectPath.getControllerImplMainPath());
    }

    private void generateFile(JavaSource javaClassSource, Path entityMainPath){
        try {
            File archivo = new File(entityMainPath.toFile().getPath() + "\\" + javaClassSource.getName()+ ".java");
            FileWriter escritor = new FileWriter(archivo);
            escritor.write(javaClassSource.toString());
            escritor.close();
            System.out.println("Archivo generado correctamente en: " + archivo);
        } catch (IOException e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
        }
    }


    public void print(JavaClassDecorator javaClassDecorator) {
        System.out.println(javaClassDecorator.printClass());
    }
}

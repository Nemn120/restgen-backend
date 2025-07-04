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
import java.util.Map;

@Component
public class JavaClassGenerator {

    private static final Logger logger = LogManager.getLogger(JavaClassGenerator.class);

    public void generate(Project project, MavenProjectPath mavenProjectPath) {
        long startTime = System.currentTimeMillis();

        List<DocketField> docketFields = new ArrayList<>();
        List<JavaClass> genericJavaClasses = new ArrayList<>();

        project.getClasses().parallelStream().forEach(javaClass -> {
            JavaClass javaClassGenerate = generateJavaFiles(javaClass, mavenProjectPath);
            if (javaClassGenerate != null) {
                synchronized (docketFields) {
                    DocketField docketField = createDocketField(javaClassGenerate);
                    docketFields.add(docketField);
                }
                synchronized (genericJavaClasses) {
                    genericJavaClasses.add(javaClassGenerate);
                }
            }
        });

        generateDTOAndMapperFiles(genericJavaClasses, mavenProjectPath);
        SwaggerJavaClass documentationSwagger = createSwaggerDocumentation(project.getProperties(), docketFields);
        generateSwaggerConfig(documentationSwagger, mavenProjectPath);

        long endTime = System.currentTimeMillis();
        logger.info("Tiempo total de generación: " + (endTime - startTime) + " ms");
    }

    private JavaClass generateJavaFiles(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        if (javaClass != null) {
            Map<Path, JavaClassProcessor> processors = Map.of(
                    mavenProjectPath.getEntityMainPath(), new EntityClassGenerator<>(javaClass, mavenProjectPath),
                    mavenProjectPath.getServiceMainPath(), new ServiceClassGenerator<>(javaClass, mavenProjectPath),
                    mavenProjectPath.getServiceImplMainPath() ,new ServiceImplClassGenerator<>(javaClass, mavenProjectPath),
                    mavenProjectPath.getRepositoryMainPath(), new RepositoryClassGenerator<>(javaClass, mavenProjectPath),
                    mavenProjectPath.getControllerMainPath(),new ControllerClassGenerator<>(javaClass, mavenProjectPath),
                    mavenProjectPath.getControllerImplMainPath(),new ControllerImplClassGenerator<>(javaClass, mavenProjectPath)
            );

            processors.entrySet()
                    .parallelStream()
                    .forEach(   entry -> {
                        Path entityMainPath = entry.getKey();
                        JavaClassProcessor processor = entry.getValue();
                        processor.decorate();
                        generateFile(processor.getJavaClassSource(), entityMainPath);
                    });
        }
        return javaClass;
    }

    private void generateFile(JavaSource javaClassSource, Path entityMainPath){
        try {
            File archivo = new File(entityMainPath.toFile().getPath() + "\\" + javaClassSource.getName() + ".java");
            FileWriter escritor = new FileWriter(archivo);
            escritor.write(javaClassSource.toString());
            escritor.close();
            System.out.println("Archivo generado correctamente en: " + archivo);
        } catch (IOException e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
        }
    }

    private void generateDTOAndMapperFiles(List<JavaClass> genericJavaClasses, MavenProjectPath mavenProjectPath) {
        for (JavaClass javaClass : genericJavaClasses) {
            updateDTOProperties(javaClass);
            generateDTO(javaClass, mavenProjectPath);
            generateMapper(javaClass, mavenProjectPath);
        }
    }

    private void generateDTO(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        final JavaClassSource dto = Roaster.create(JavaClassSource.class);
        DTOGenerator serviceClassDecorator = new DTOGenerator(dto, javaClass, mavenProjectPath);
        serviceClassDecorator.decorate();
        generateFile(serviceClassDecorator.getJavaClassSource(), mavenProjectPath.getDTOMainPath());
    }

    private void generateMapper(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        final JavaInterfaceSource mapper = Roaster.create(JavaInterfaceSource.class);
        MapperGenerator mapperDecorator = new MapperGenerator(mapper, javaClass, mavenProjectPath);
        mapperDecorator.decorate();
        generateFile(mapperDecorator.getJavaClassSource(), mavenProjectPath.getMapperMainPath());
    }

    private void updateDTOProperties(JavaClass javaClass) {
        for (Column column : javaClass.getEntity().getColumns()) {
            if (column.getRelation() != null && column.getRelation().getType() != null) {
                String name = column.getProperty().getName();
                String dtoRelationName = GeneratorUtil.concatRelationAndKeyRelation(name, "id");
                if (column.getPropertyDTO() == null) {
                    column.setPropertyDTO(new Property());
                }
                column.getPropertyDTO().setName(dtoRelationName);
                column.getPropertyDTO().setRelationNameWithId(name + ".id");
                column.getPropertyDTO().setType(DataTypes.LONG.getName());
            }
        }
    }

    private DocketField createDocketField(JavaClass javaClass) {
        return DocketField.builder()
                .apiName(javaClass.getApiName())
                .name(GeneratorUtil.convertCamelToSnakeCaseLower(javaClass.getName()))
                .camelCaseName(GeneratorUtil.convertUpperFisrtLettersAndCamelCase(javaClass.getName()))
                .build();
    }

    private SwaggerJavaClass createSwaggerDocumentation(ProjectProperties properties, List<DocketField> docketFields) {
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

        return documentationSwagger;
    }

    private void generateSwaggerConfig(SwaggerJavaClass docProperties, MavenProjectPath mavenProjectPath) {
        if (docProperties != null) {
            final JavaClassSource swaggerClass = Roaster.create(JavaClassSource.class);
            SwaggerGenerator swaggerGenerator = new SwaggerGenerator(swaggerClass, docProperties, mavenProjectPath);
            swaggerGenerator.decorate();
            generateFile(swaggerGenerator.getJavaClassSource(), mavenProjectPath.getConfigMainPath());
        }
    }
}
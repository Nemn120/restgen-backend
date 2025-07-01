package es.achavez.miw.tfm.restgen.generator.application.service.generator;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.classes.*;
import es.achavez.miw.tfm.restgen.generator.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JavaClassGenerator2 {
/*
    private static final Logger logger = LogManager.getLogger(JavaClassGenerator.class);

    public void generate(Project project, MavenProjectPath mavenProjectPath) {
        List<DocketField> docketFields = new ArrayList<>();
        List<JavaClass> genericJavaClasses = new ArrayList<>();

        for (JavaClass javaClass : project.getClasses()) {
            JavaClass javaClassGenerate = generateJavaFiles(javaClass, mavenProjectPath);
            if (javaClassGenerate != null) {
                DocketField docketField = createDocketField(javaClassGenerate);
                docketFields.add(docketField);
                genericJavaClasses.add(javaClassGenerate);
            }
        }

        generateDTOAndMapperFiles(genericJavaClasses, mavenProjectPath);
        SwaggerJavaClass documentationSwagger = createSwaggerDocumentation(project.getProperties(), docketFields);
        generateSwaggerConfig(documentationSwagger, mavenProjectPath);
    }

    private JavaClass generateJavaFiles(JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        if (javaClass != null) {
            List<JavaClassProcessor> processors = List.of(
                    new EntityClassGenerator<>(javaClass, mavenProjectPath),
                    new ServiceClassGenerator<>(javaClass, mavenProjectPath),
                    new ServiceImplClassGenerator<>(javaClass, mavenProjectPath),
                    new RepositoryClassGenerator<>(javaClass, mavenProjectPath),
                    new ControllerClassGenerator(javaClass, mavenProjectPath),
                    new ControllerImplClassGenerator(javaClass, mavenProjectPath)
            );

            for (JavaClassProcessor processor : processors) {
                processor.generate();
            }
        }
        return javaClass;
    }

    private void generateDTOAndMapperFiles(List<JavaClass> genericJavaClasses, MavenProjectPath mavenProjectPath) {
        for (JavaClass javaClass : genericJavaClasses) {
            updateDTOProperties(javaClass);
            new DTOGenerator(javaClass, mavenProjectPath).generate();
            new MapperGenerator(javaClass, mavenProjectPath).generate();
        }
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
            new SwaggerGenerator(docProperties, mavenProjectPath).generate();
        }
    }*/
}
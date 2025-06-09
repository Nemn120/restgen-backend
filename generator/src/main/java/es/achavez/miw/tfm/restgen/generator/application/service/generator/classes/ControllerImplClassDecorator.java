package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.*;

public class ControllerImplClassDecorator<T extends JavaClassSource> extends JavaClassAbstractDecorator<T> {

    public ControllerImplClassDecorator(T javaClassSource, JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        super(javaClassSource, javaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.CONTROLLER_IMPL;
    }

    @Override
    public void decorate() {
        super.decorate();
        String controllerImplName = packageDirectory.getNameClassLayer(DirectoryLayerPath.CONTROLLER_IMPL);
        String controllerName = packageDirectory.getNameClassLayer(DirectoryLayerPath.CONTROLLER);
        String dtoName = packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY) + "DTO";

        getJavaClassSource().setName(controllerImplName);
        getJavaClassSource().addInterface(controllerName);
        addAnnotationAndImport(AnnotationPersistence.REST_CONTROLLER);
        addAnnotationAndImport(AnnotationPersistence.REQUEST_MAPPING).setStringValue("value", "/" + javaClass.getName().toLowerCase());

        addImport(DirectoryLayerPath.CONTROLLER);
        addSubPackageImport(RESPONSE_CUSTOM_PAGE);
        addSubPackageImport(GENERIC_RESPONSE);
        addSubPackageImport(RESPONSE_ENTITY_UTIL);
        addImport(AUTOWIRED);
        addImport(RESPONSE_ENTITY_UTIL);
        addImport(RESPONSE_ENTITY);
        addImport(HTTP_STATUS);
        addImport(LOGGER);
        addImport(LOGGER_FACTORY);
        addImport(SORT_DIRECTION);
        addImport(LIST);

        getJavaClassSource().addImport("org.springframework.data.domain.PageRequest");
        getJavaClassSource().addImport("org.springframework.data.domain.Pageable");

        addImport(BIN_ANNOTATION);
        addImport(DirectoryLayerPath.SERVICE);
        addImport(DirectoryLayerPath.DTO);

        getJavaClassSource().addField()
                .setName("logger")
                .setType(LOGGER.getAnnotationName())
                .setVisibility(Visibility.PRIVATE)
                .setLiteralInitializer("LoggerFactory.getLogger(" + controllerImplName + ".class)");

        getJavaClassSource().addField()
                .setName("service")
                .setType(packageDirectory.getNameClassLayer(DirectoryLayerPath.SERVICE))
                .setVisibility(Visibility.PRIVATE)
                .addAnnotation(AnnotationPersistence.AUTOWIRED.getAnnotationName());

        generateFindByIdMethod(dtoName);
        generateFindAllMethod(dtoName);
        generateSaveMethod(dtoName);
        generateUpdateMethod(dtoName);
        generateDeleteMethod();
        generateSearchMethod(dtoName);
    }

    private void generateFindByIdMethod(String dtoName) {
        MethodSource<JavaClassSource> method = getJavaClassSource().addMethod();
        method.setName("findById");
        method.setReturnType("ResponseEntity<GenericResponse<" + dtoName + ">>");
        method.setVisibility(Visibility.PUBLIC);
        method.addAnnotation(AnnotationPersistence.GET_MAPPING.getAnnotationName());
        method.addParameter("Long id", "");
        method.setBody("logger.info(\"call " + getJavaClassSource().getName() + " :: findById()\");\n" +
                       dtoName + " dto = service.findById(id);\n" +
                       "return ResponseEntityUtil.createResponse(dto, \"" + javaClass.getName() + " found\", HttpStatus.OK);");
    }

    private void generateFindAllMethod(String dtoName) {
        MethodSource<JavaClassSource> method = getJavaClassSource().addMethod();
        method.setName("findAll");
        method.setReturnType("ResponseEntity<GenericResponse<List<" + dtoName + ">>>");
        method.setVisibility(Visibility.PUBLIC);
        method.addAnnotation(AnnotationPersistence.GET_MAPPING.getAnnotationName());
        method.setBody("logger.info(\"call " + getJavaClassSource().getName() + " :: findAll()\");\n" +
                       "List<" + dtoName + "> dtos = service.findAll();\n" +
                       "return ResponseEntityUtil.createResponse(dtos, \"List of " + javaClass.getName() + "\", HttpStatus.OK);");
    }

    private void generateSaveMethod(String dtoName) {
        MethodSource<JavaClassSource> method = getJavaClassSource().addMethod();
        method.setName("save");
        method.setReturnType("ResponseEntity<GenericResponse<" + dtoName + ">>");
        method.setVisibility(Visibility.PUBLIC);
        method.addAnnotation(AnnotationPersistence.POST_MAPPING.getAnnotationName());
        method.addParameter(dtoName + " obj", "");
        method.setBody("logger.info(\"call " + getJavaClassSource().getName() + " :: save()\");\n" +
                       dtoName + " dto = service.save(obj);\n" +
                       "return ResponseEntityUtil.createResponse(dto, \"" + javaClass.getName() + " save success\", HttpStatus.CREATED);");
    }

    private void generateUpdateMethod(String dtoName) {
        MethodSource<JavaClassSource> method = getJavaClassSource().addMethod();
        method.setName("update");
        method.setReturnType("ResponseEntity<GenericResponse<" + dtoName + ">>");
        method.setVisibility(Visibility.PUBLIC);
        method.addAnnotation(AnnotationPersistence.PUT_MAPPING.getAnnotationName());
        method.addParameter("Long id", "");
        method.addParameter(dtoName + " obj", "");
        method.setBody("logger.info(\"call " + getJavaClassSource().getName() + " :: update()\");\n" +
                       "obj.setId(id);\n" +
                       dtoName + " dto = service.update(obj);\n" +
                       "return ResponseEntityUtil.createResponse(dto, \"" + javaClass.getName() + " update success\", HttpStatus.OK);");
    }

    private void generateDeleteMethod() {
        MethodSource<JavaClassSource> method = getJavaClassSource().addMethod();
        method.setName("delete");
        method.setReturnType("ResponseEntity<Void>");
        method.setVisibility(Visibility.PUBLIC);
        method.addAnnotation(AnnotationPersistence.DELETE_MAPPING.getAnnotationName());
        method.addParameter("Long id", "");
        method.setBody("logger.info(\"call " + getJavaClassSource().getName() + " :: delete()\");\n" +
                       "service.delete(id);\n" +
                       "return ResponseEntityUtil.createEmptyResponse(\"" + javaClass.getName() + " delete success\", HttpStatus.OK);");
    }

    private void generateSearchMethod(String dtoName) {
        MethodSource<JavaClassSource> method = getJavaClassSource().addMethod();
        method.setName("search");
        method.setReturnType("ResponseEntity<CustomPage<" + dtoName + ">>");
        method.setVisibility(Visibility.PUBLIC);
        method.addAnnotation(AnnotationPersistence.GET_MAPPING.getAnnotationName());
        method.addParameter("int page", "");
        method.addParameter("int size", "");
        method.addParameter("String sortField", "");
        method.addParameter("Direction direction", "");
        method.addParameter(dtoName + " filterDto", "");
        method.setBody("Pageable pageable = PageRequest.of(page - 1, size);\n" +
                       "CustomPage<" + dtoName + "> dtos = service.findByAttributesAndPaginationAndSort(filterDto, pageable, sortField, direction);\n" +
                       "return ResponseEntityUtil.createCustomPageResponse(dtos, \"Search found\", HttpStatus.OK);");
    }
}
package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.*;

public class ControllerClassGenerator<T extends JavaInterfaceSource> extends JavaClassTemplate<T> {

    public ControllerClassGenerator(T javaClassSource, JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        super(javaClassSource, javaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.CONTROLLER;
    }

    @Override
    public void decorate() {
        super.decorate();
        String interfaceName = packageDirectory.getNameClassLayer(DirectoryLayerPath.CONTROLLER);
        String dtoName = packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY) + "DTO";

        getJavaClassSource().setName(interfaceName);
        addAnnotationAndImport(AnnotationPersistence.SECURITY_REQUIREMENT).setStringValue("name", "JWT");
        addImport(OPERATION);
        addImport(SORT_DIRECTION);
        addSubPackageImport(RESPONSE_CUSTOM_PAGE);
        addSubPackageImport(GENERIC_RESPONSE);
        addImport(RESPONSE_ENTITY);

        addImport(LIST);
        addImport(DirectoryLayerPath.DTO);

        generateFindByIdMethod(dtoName);
        generateFindAllMethod(dtoName);
        generateSaveMethod(dtoName);
        generateUpdateMethod(dtoName);
        generateDeleteMethod(dtoName);
        generateSearchMethod(dtoName);
    }

    private void generateFindByIdMethod(String dtoName) {
        MethodSource<JavaInterfaceSource> method = getJavaClassSource().addMethod();
        method.setName("findById");
        method.setReturnType("ResponseEntity<GenericResponse<" + dtoName + ">>");
        method.addAnnotation(OPERATION.getAnnotationName()).setStringValue("summary", "Buscar por Id");
        method.addParameter("Long id", "");
    }

    private void generateFindAllMethod(String dtoName) {
        MethodSource<JavaInterfaceSource> method = getJavaClassSource().addMethod();
        method.setName("findAll");
        method.setReturnType("ResponseEntity<GenericResponse<List<" + dtoName + ">>>");
        method.addAnnotation(OPERATION.getAnnotationName()).setStringValue("summary", "Listar");
    }

    private void generateSaveMethod(String dtoName) {
        MethodSource<JavaInterfaceSource> method = getJavaClassSource().addMethod();
        method.setName("save");
        method.setReturnType("ResponseEntity<GenericResponse<" + dtoName + ">>");
        method.addAnnotation(OPERATION.getAnnotationName()).setStringValue("summary", "Guardar");
        method.addParameter(dtoName + " obj", "");
    }

    private void generateUpdateMethod(String dtoName) {
        MethodSource<JavaInterfaceSource> method = getJavaClassSource().addMethod();
        method.setName("update");
        method.setReturnType("ResponseEntity<GenericResponse<" + dtoName + ">>");
        method.addAnnotation(OPERATION.getAnnotationName()).setStringValue("summary", "Actualizar");
        method.addParameter("Long id", "");
        method.addParameter(dtoName + " obj", "");
    }

    private void generateDeleteMethod(String dtoName) {
        MethodSource<JavaInterfaceSource> method = getJavaClassSource().addMethod();
        method.setName("delete");
        method.setReturnType("ResponseEntity<Void>");
        method.addAnnotation(OPERATION.getAnnotationName()).setStringValue("summary", "Eliminar");
        method.addParameter("Long id", "");
    }

    private void generateSearchMethod(String dtoName) {
        MethodSource<JavaInterfaceSource> method = getJavaClassSource().addMethod();
        method.setName("search");
        method.setReturnType("ResponseEntity<CustomPage<" + dtoName + ">>");
        method.addAnnotation(OPERATION.getAnnotationName()).setStringValue("summary", "Buscar por campos");
        method.addParameter("int page", "");
        method.addParameter("int size", "");
        method.addParameter("String sortField", "");
        method.addParameter("Direction direction", "");
        method.addParameter(dtoName + " filterDto", "");
    }
}
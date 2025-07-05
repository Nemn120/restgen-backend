package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.DataTypes;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.*;


public class ServiceClassGenerator<T extends JavaInterfaceSource> extends JavaClassTemplate<T> {

    public ServiceClassGenerator(JavaClass JavaClass, MavenProjectPath mavenProjectPath) {
        super((T) Roaster.create(JavaInterfaceSource.class), JavaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.SERVICE;
    }

    @Override
    public void decorate() {
        super.decorate();
        String camelCaseName = packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY);
        String nameClassLayer = packageDirectory.getNameClassLayer(DirectoryLayerPath.SERVICE);
        getJavaClassSource().setName(nameClassLayer);
        getJavaClassSource().addInterface(GENERIC_SERVICE.getAnnotationName() +
                                          "<" +
                                          camelCaseName +
                                          ", " +
                                          packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO) +
                                          ", "+
                                          DataTypes.LONG.getName()
                                          + ">");

        addImport(DirectoryLayerPath.ENTITY);
        addImport(DirectoryLayerPath.DTO);

        String upperCase = nameClassLayer.substring(1);
        char charAt = nameClassLayer.charAt(0);
        String initLetter = String.valueOf(charAt).toLowerCase();
        FieldSource<JavaInterfaceSource> fieldSource = getJavaClassSource().addField();
        fieldSource.setType(String.class);
        fieldSource.setName("BEAN_NAME");
        fieldSource.setStringInitializer(initLetter.concat(upperCase));

    }
}
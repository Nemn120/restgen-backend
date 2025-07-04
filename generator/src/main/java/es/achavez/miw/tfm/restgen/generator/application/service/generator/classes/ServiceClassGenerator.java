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
        getJavaClassSource().setName(packageDirectory.getNameClassLayer(DirectoryLayerPath.SERVICE));
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

        String upperCase = camelCaseName.substring(1);
        char charAt = camelCaseName.charAt(0);
        String initLetter = String.valueOf(charAt).toLowerCase();
        FieldSource<JavaInterfaceSource> fieldSource = getJavaClassSource().addField();
        fieldSource.setType(String.class);
        fieldSource.setName("BEAN_NAME");
        fieldSource.setStringInitializer(initLetter.concat(upperCase));

    }

  /*  @Override
    protected void addPackageClass() {
        PackageDirectoryLayer layer = packageDirectory.getLayer(DirectoryLayerPath.SERVICE);
        getJavaClassSource().setPackage(layer.getPackagePath());
    }*/
}
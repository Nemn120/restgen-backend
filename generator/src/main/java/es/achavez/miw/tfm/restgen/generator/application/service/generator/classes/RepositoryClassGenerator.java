package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;


import es.achavez.miw.tfm.restgen.generator.application.service.generator.GeneratorUtil;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.DataTypes;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

public class RepositoryClassGenerator<T extends JavaInterfaceSource> extends JavaClassTemplate<T> {

    public RepositoryClassGenerator(JavaClass JavaClass, MavenProjectPath mavenProjectPath) {
        super((T) Roaster.create(JavaInterfaceSource.class), JavaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.REPOSITORY;
    }

    @Override
    public void decorate() {
        super.decorate();

        String camelCaseName = GeneratorUtil.snakeCaseToUpperCamelCase(javaClass.getName());
        getJavaClassSource().setName(packageDirectory.getNameClassLayer(DirectoryLayerPath.REPOSITORY));
        addImport(DirectoryLayerPath.ENTITY);
        getJavaClassSource().addInterface(
                AnnotationPersistence.GENERIC_REPOSITORY.getAnnotationName() +
                "<" +
                camelCaseName + ", " +
                DataTypes.LONG.getName() +
                ">");

    }
}

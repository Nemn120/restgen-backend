package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;


import es.achavez.miw.tfm.restgen.generator.application.service.generator.GeneratorUtil;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.DataTypes;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.GENERIC_SERVICE;

public class RepositoryClassDecorator<T extends JavaInterfaceSource> extends JavaClassAbstractDecorator<T> {

    private static Logger logger = LogManager.getLogger(RepositoryClassDecorator.class);

    public RepositoryClassDecorator(T javaInterfaceSource, JavaClass JavaClass, MavenProjectPath mavenProjectPath) {
        super(javaInterfaceSource, JavaClass, mavenProjectPath);
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

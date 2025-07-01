package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;


import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.directory.PackageDirectory;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.directory.PackageDirectoryLayer;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaSource;

public abstract class JavaClassTemplate<T extends JavaSource> implements JavaClassProcessor<T> {

    private static Logger logger = LogManager.getLogger(JavaClassTemplate.class);

    protected T javaClassSource;

    protected JavaClass javaClass;

    protected DirectoryLayerPath directoryLayerPath;

    protected PackageDirectory packageDirectory;
    protected MavenProjectPath mavenProjectPath;

    @Override
    public void decorate() {
        logger.info("decorate: decorando: "+ this.getClass().getSimpleName());
        addPackageClass();
    }

    public JavaClassTemplate(T javaClassSource, JavaClass javaClass, MavenProjectPath mavenProjectPath) {
        this.javaClassSource = javaClassSource;
        this.javaClass = javaClass;
        this.mavenProjectPath = mavenProjectPath;
        this.packageDirectory = new PackageDirectory(this.javaClass, mavenProjectPath);
    }

    protected void addImport(AnnotationPersistence table) {
        getJavaClassSource().addImport(table.getPackageName());
    }

    protected void addSubPackageImport(AnnotationPersistence subPackage) {
        String groupIdPackage = mavenProjectPath.getGroupIdPackage();
        getJavaClassSource().addImport(groupIdPackage.concat(subPackage.getPackageName()));
    }

    protected void addImport(DirectoryLayerPath layerPath) {

        PackageDirectoryLayer layer = packageDirectory.getLayer(layerPath);
        logger.info("addImport: Agregando importación "+ layer.getImportPath());
        getJavaClassSource().addImport(layer.getImportPath());
    }

    protected AnnotationSource addAnnotationAndImport(AnnotationPersistence entity) {
        addImport(entity);
        return getJavaClassSource().addAnnotation(entity.getAnnotationName());
    }

    protected void addPackageClass(){
        PackageDirectoryLayer layer = packageDirectory.getLayer(directoryLayerPath);
        getJavaClassSource().setPackage(layer.getPackagePath());
    }

    @Override
    public String printClass() {
        return getJavaClassSource().toString();
    }

    @Override
    public T getJavaClassSource() {
        return javaClassSource;
    }
}

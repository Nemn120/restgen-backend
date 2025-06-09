package es.achavez.miw.tfm.restgen.generator.application.service.generator.directory;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.GeneratorUtil;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;

import java.util.HashMap;
import java.util.Map;


public class PackageDirectory {

    private String javaClassName;
    private MavenProjectPath packageName;

    private Map<DirectoryLayerPath, PackageDirectoryLayer> layersPath;

    public PackageDirectory(JavaClass JavaClass, MavenProjectPath mavenProjectPath) {
        this.packageName = mavenProjectPath;
        String camelCaseName = GeneratorUtil.snakeCaseToUpperCamelCase(JavaClass.getName());
        this.javaClassName = camelCaseName;
        this.layersPath = initializeLayers();
    }

    private Map<DirectoryLayerPath, PackageDirectoryLayer> initializeLayers() {
        layersPath = new HashMap<>();
        addLayerPath(DirectoryLayerPath.SERVICE);
        addLayerPath(DirectoryLayerPath.SERVICE_IMPL);
        addLayerPath(DirectoryLayerPath.REPOSITORY);
        addLayerPath(DirectoryLayerPath.ENTITY);
        addLayerPath(DirectoryLayerPath.CONTROLLER);
        addLayerPath(DirectoryLayerPath.CONTROLLER_IMPL);
        addLayerPath(DirectoryLayerPath.DTO);
        addLayerPath(DirectoryLayerPath.MAPPER);
        return layersPath;
    }

    private void addLayerPath(DirectoryLayerPath layerPath) {
        layersPath.put(layerPath, createPackageDirectoryLayer(layerPath));
    }

    private PackageDirectoryLayer createPackageDirectoryLayer(DirectoryLayerPath directoryLayerPath) {
        return new PackageDirectoryLayer(packageName, javaClassName, directoryLayerPath);
    }

    public PackageDirectoryLayer getLayer(DirectoryLayerPath directoryLayerPath){
        return this.layersPath.get(directoryLayerPath);
    }

    public String getNameClassLayer(DirectoryLayerPath directoryLayerPath){
        PackageDirectoryLayer packageDirectoryLayer = this.layersPath.get(directoryLayerPath);
        if(packageDirectoryLayer != null){
            return packageDirectoryLayer.getNameClassLayer();
        }
        throw new IllegalArgumentException("No existe esa capa");
    }
}

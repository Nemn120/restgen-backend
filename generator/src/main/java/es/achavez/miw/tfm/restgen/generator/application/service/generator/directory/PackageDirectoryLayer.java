package es.achavez.miw.tfm.restgen.generator.application.service.generator.directory;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class PackageDirectoryLayer {

    private String nameClassLayer;
    private String packagePath; // Ubicación del paquete en formato separado por puntos
    private String importPath;  // Ruta de importación completa
    private String directoryPath; // Ruta del paquete separada por '\'

    public PackageDirectoryLayer(MavenProjectPath mavenProjectPath,
                                 String javaClassName,
                                 DirectoryLayerPath directoryLayerPath) {

        String packagePath = mavenProjectPath.getGroupIdWithoutDashes() + directoryLayerPath.getSubPath();
        String directoryPath = mavenProjectPath.getGroupIdPath().toString().replace(".", "\\") + directoryLayerPath.getSubPath().replace(".", "\\");

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("El directorio especificado no existe o no es un folder: " + directoryPath);
        }

        String nameClassLayer = javaClassName.concat(directoryLayerPath.getClassName());
        String importPath = packagePath + "." + nameClassLayer;

        this.packagePath = packagePath;
        this.importPath = importPath;
        this.nameClassLayer = nameClassLayer;
        this.directoryPath = directoryPath;
    }
}
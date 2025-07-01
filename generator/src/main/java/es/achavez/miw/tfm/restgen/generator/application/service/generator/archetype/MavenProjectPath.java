package es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class MavenProjectPath {

    private static final Logger LOG = LoggerFactory.getLogger(MavenProjectPath.class);

    private static final String PATH_FORMAT = "src%s%s%s%s%s";

    private MavenPropertiesArchetype mavenPropertiesArchetype;
    private final Path projectPath;

    private final Path javaMainPath;
    private final Path javaTestPath;
    private final Path resourcesMainPath;
    private final Path resourcesTestPath;

    private String groupIdWithoutDashes;

    public MavenProjectPath(Path basePath, MavenPropertiesArchetype mavenPropertiesArchetype) {
        LOG.info("MavenProjectPath :: Estableciendo valores de construcción");
        this.projectPath = basePath.resolve(mavenPropertiesArchetype.getArtifactId());
        this.mavenPropertiesArchetype = mavenPropertiesArchetype;
        this.javaMainPath = resolvePathByDirectory("main", "java");
        this.javaTestPath = resolvePathByDirectory("test", "java");
        this.resourcesMainPath = resolvePathByDirectory("main", "resources");
        this.resourcesTestPath = resolvePathByDirectory("test", "resources");
        this.groupIdWithoutDashes = mavenPropertiesArchetype.getGroupId();
    }

    private Path resolvePathByDirectory(String... directories) {
        return projectPath.resolve(String.format(PATH_FORMAT,
                File.separator, directories[0], File.separator, directories[1], File.separator));
    }

    private Path concatGroupIdPath(Path path) {
        return path.resolve(getGroupIdPackage().replace(".", File.separator));
    }

    public String getGroupIdPackage() {
        return groupIdWithoutDashes;
    }


    private Path concatSubPackagePath(Path path, String subPackage) {
        return path.resolve(subPackage);
    }

    public Path getServiceMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "service");
    }

    public Path getServiceImplMainPath() {
        return concatSubPackagePath(getServiceMainPath(), "impl");
    }

    public Path getControllerMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "controller");
    }

    public Path getControllerImplMainPath() {
        return concatSubPackagePath(getControllerMainPath(), "impl");
    }

    public Path getEntityMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "entity");
    }

    public Path getRepositoryMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "repository");
    }

    public Path getMapperMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "mapper");
    }

    public Path getDTOMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "dto");
    }

    public Path getGroupIdPath() {
        return concatGroupIdPath(javaMainPath);
    }

    public Path getConfigMainPath() {
        return concatSubPackagePath(concatGroupIdPath(javaMainPath), "config");
    }

    public String getGroupIdWithoutDashes() {
        return groupIdWithoutDashes;
    }

    public MavenPropertiesArchetype getMavenPropertiesArchetype() {
        return mavenPropertiesArchetype;
    }
}

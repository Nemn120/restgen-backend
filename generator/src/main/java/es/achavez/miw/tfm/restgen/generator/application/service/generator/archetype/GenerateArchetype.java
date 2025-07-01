package es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class GenerateArchetype {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateArchetype.class);

    private final MavenProjectPath mavenProjectPath;

    public GenerateArchetype(MavenProjectPath mavenProjectPath) {
        this.mavenProjectPath = mavenProjectPath;
    }

    public void createProjectFromArchetype(Path basePath) throws IOException {
        LOG.info("GeneratedProject :: createProjectFromArchetype");
        List<String> command = createMavenCommand();
        LOG.info("limpiando directorio ::: ");
        cleanDirectoryGenerated(basePath);
        LOG.info("Ejecutando comando: " + StringUtils.join(command, StringUtils.SPACE));

        try {
            Process process = new ProcessBuilder(command)
                    .directory(basePath.toFile())
                    .inheritIO().start();

            int result = process.waitFor();
            if (result != 0) {
                throw new IOException(format("Failed to execute command with result code '%s'", result));
            }
        } catch (IOException | InterruptedException e) {
            throw new IOException(e);
        }
    }

    private List<String> createMavenCommand() {
        List<String> command = new ArrayList<>();
        if (SystemUtils.IS_OS_WINDOWS) {
            command.add("cmd");
            command.add("/C");
        }
        command.add("mvn");
        command.add("archetype:generate");
        command.add("-DarchetypeCatalog=local");
        command.add("-DarchetypeGroupId=com.fchavez.archetype.api");
        command.add("-DarchetypeArtifactId=archetype-spring-api");
        command.add("-DarchetypeVersion=1.0.0");
        command.add("-DgroupId=" + mavenProjectPath.getMavenPropertiesArchetype().getGroupId());
        command.add("-DartifactId=" + mavenProjectPath.getMavenPropertiesArchetype().getArtifactId());
        command.add("-Dversion=" + mavenProjectPath.getMavenPropertiesArchetype().getVersion());
        command.add("-DappName=" + mavenProjectPath.getMavenPropertiesArchetype().getAppName());
        command.add("-DbasePath=" + mavenProjectPath.getMavenPropertiesArchetype().getBasePath());

        if (mavenProjectPath.getMavenPropertiesArchetype().getPort() != null) {
            command.add("-Dport=" + mavenProjectPath.getMavenPropertiesArchetype().getPort());
        } else {
            command.add("-Dport=" + 8080);
        }
        command.add("-DinteractiveMode=false");
        return command;
    }

    private void cleanDirectoryGenerated(Path path) throws IOException {
        if (path.toFile().exists())
            FileUtils.cleanDirectory(path.toFile());
    }

    public static void main(String[] args) {
        MavenPropertiesArchetype mavenPropertiesArchetype = new MavenPropertiesArchetype(
                "com.fchavez.archetype.api",
                "generate-spring-api",
                "1.0.0",
                "MyApp",
                "/base/path",
                8080
        );
        MavenProjectPath mavenProjectPath = new MavenProjectPath(Path.of("generate"), mavenPropertiesArchetype);
        GenerateArchetype generateArchetype = new GenerateArchetype(mavenProjectPath);
        try {
            generateArchetype.createProjectFromArchetype(Path.of("generate"));
        } catch (IOException e) {
            LOG.error("Error creating project from archetype: ", e);
        }
    }

}

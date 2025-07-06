package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.in.GeneratorUsesCases;
import es.achavez.miw.tfm.restgen.generator.application.port.out.FileRepository;
import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.JavaClassGenerator;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.ConfigurationPath;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.GenerateArchetype;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenPropertiesArchetype;
import es.achavez.miw.tfm.restgen.generator.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class GeneratorService implements GeneratorUsesCases {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorService.class);

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private JavaClassGenerator javaClassGenerator;
    @Autowired
    private FileRepository fileRepository;

    @Override
    public void generate(String projectId) throws IOException {
        LOG.info("EasyRestGeneratorService :: execute()");

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
        if(project.getClasses() == null || project.getClasses().isEmpty()) {
            LOG.warn("GeneratorService :: No classes found in the project, aborting generation");
            return;
        }

        long startTime = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        this.generateProject(project, uuid);
        uploadGeneratedFiles(uuid);
        project.setStatus(ProjectStatus.GENERATED);
        project.setUrlRepository(uuid);
        project.setUpdateDate(LocalDateTime.now());
        projectRepository.save(project);
        LOG.info("GeneratorService :: generacion exitosa del proyecto");
        long finalTime = System.currentTimeMillis();
        LOG.info("GeneratorService :: Tiempo de ejecucion: " + (finalTime - startTime) + " milliseconds");
    }

    private void uploadGeneratedFiles(String uuid) {
        LOG.info("GeneratorService :: uploadGeneratedFiles()");
        ConfigurationPath configurationPath = new ConfigurationPath(uuid);
        Path applicationPath = configurationPath.getApplicationPath();
        fileRepository.upload(uuid, applicationPath);
        LOG.info("GeneratorService :: Archivos generados y subidos correctamente");
    }

    private void generateProject(Project project, String uuid) throws IOException {
        ConfigurationPath configurationPath = new ConfigurationPath(uuid);
        MavenProjectPath mavenProjectPath = createMavenProjectPath(project, configurationPath);
        GenerateArchetype generateArchetype = new GenerateArchetype(mavenProjectPath);
        generateArchetype.createProjectFromArchetype(configurationPath.getApplicationPath());

        javaClassGenerator.generate(project, mavenProjectPath);
    }

    private MavenProjectPath createMavenProjectPath(Project project, ConfigurationPath configurationPath) {
        MavenPropertiesArchetype mavenPropertiesArchetype = mapInMavenProperties(project);
        Path basePath = configurationPath.getApplicationPath();
        return new MavenProjectPath(basePath, mavenPropertiesArchetype);
    }

    private MavenPropertiesArchetype mapInMavenProperties(Project project) {
        ProjectProperties properties = project.getProperties();
        MavenProperties mavenProperties = properties.getMaven();
        ApplicationProperties applicationProperties = properties.getApplication();
        SecurityProperties securityProperties = properties.getSecurity();
        String groupId = mavenProperties.getGroupId();
        String artifactId = mavenProperties.getArtifactId();
        String version = mavenProperties.getVersion();
        Integer port = applicationProperties.getPort();
        String basePath = applicationProperties.getBasePath();
        String name = project.getName();
        String secretKey = securityProperties.getSecretKey();
        return new MavenPropertiesArchetype(groupId, artifactId, version, name, basePath, port, secretKey);
    }
}

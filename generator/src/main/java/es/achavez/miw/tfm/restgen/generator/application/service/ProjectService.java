package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.in.ProjectUsesCases;
import es.achavez.miw.tfm.restgen.generator.application.port.out.FileRepository;
import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService implements ProjectUsesCases {

    @Autowired
    private  ProjectRepository projectRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    public List<Project> findAllPublic() {
        return projectRepository.findAllPublic();
    }

    @Override
    public Project findById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Project save(Project project) {
        project.setStatus(ProjectStatus.CREATED);
        project.setCreationDate(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @Override
    public Project update(Project project) {
        Project byId = this.findById(project.getId());
        byId.setProperties(project.getProperties());
        byId.setName(project.getName());
        byId.setDescription(project.getDescription());
        byId.setUrlRepository(project.getUrlRepository());
        byId.setIsPrivate(project.getIsPrivate());
        byId.setUpdateDate(LocalDateTime.now());
        byId.setUpdateUser(project.getUpdateUser());
        return projectRepository.save(byId);
    }

    @Override
    public void deleteById(String id) {
        projectRepository.deleteById(id);
    }

    @Override
    public String cloneProject(String id) {
        Project project = this.findById(id);
        if (project.getStatus() != ProjectStatus.GENERATED) {
            throw new NotFoundException("Project with id " + id + " is not generated yet.");
        }
        Project clonedProject = project.clone();
        Project save = this.save(clonedProject);
        return save.getId();
    }

    @Override
    public InputStream download(String id) throws IOException {
        Project byId = this.findById(id);
        if (byId.getStatus() != ProjectStatus.GENERATED) {
            throw new NotFoundException("Project with id " + id + " is not generated yet.");
        }

        File zipFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + ".zip");
        try {
            try (FileOutputStream fos = new FileOutputStream(zipFile)) {
                fileRepository.downloadZip(byId.getUrlRepository()).transferTo(fos);
            }
        } catch (Exception e) {
            throw new IOException("Error al descargar el archivo ZIP", e);
        }
        return new FileInputStream(zipFile);
    }

    @Override
    public Project findDiagramPlantUmlById(String id) {
        return projectRepository.findDiagramPlantUmlById(id);
    }

    @Override
    public void uploadToGitHub(String projectId, GitHubUploadDto dto) throws IOException {
        Project project = this.findById(projectId);
        if (project.getStatus() != ProjectStatus.GENERATED) {
            throw new IllegalArgumentException("El proyecto no está generado.");
        }

        GithubRepository repository;
        if(project.getGithubRepository() == null || StringUtils.isBlank(project.getGithubRepository().getUrl())){

        }
        repository = fileRepository.uploadGithub(project.getUrlRepository(), dto);

    }

    @Override
    public List<Project> findByUser(String token) {
        String extractedToken = jwtService.extractToken(token);
        String user = jwtService.user(extractedToken);
        return this.projectRepository.findByCreationUser(user);
    }
}

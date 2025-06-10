package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.out.FileRepository;
import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ProjectService {

    @Autowired
    private  ProjectRepository projectRepository;
    @Autowired
    private FileRepository fileRepository;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    public Project save(Project project) {
        project.setStatus(ProjectStatus.CREATED);
        return projectRepository.save(project);
    }

    public Project update(Project project) {
        Project byId = this.findById(project.getId());
        byId.setProperties(project.getProperties());
        byId.setName(project.getName());
        byId.setDescription(project.getDescription());
        byId.setUrlRepository(project.getUrlRepository());
        byId.setIsPrivate(project.getIsPrivate());
        return projectRepository.save(project);
    }

    public void deleteById(String id) {
        projectRepository.deleteById(id);
    }

    public String cloneProject(String id) {
        Project project = this.findById(id);
        Project clonedProject = project.clone();
        Project save = this.save(clonedProject);
        return save.getId();
    }

    public StreamingResponseBody download(String id) throws IOException {
        Project byId = this.findById(id);
        if (byId.getStatus() != ProjectStatus.GENERATED) {
            throw new NotFoundException("Project with id " + id + " is not generated yet.");
        }
        List<File> files = fileRepository.downloadFolder(byId.getUrlRepository());
        StreamingResponseBody responseBody = outputStream -> {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
                for (File file : files) {
                    String relativePath = file.getPath().substring(System.getProperty("java.io.tmpdir").length() + 1); // Obtiene la ruta relativa
                    zipOutputStream.putNextEntry(new ZipEntry(relativePath));
                    Files.copy(file.toPath(), zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
        };
        return responseBody;
    }
}
package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

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
}
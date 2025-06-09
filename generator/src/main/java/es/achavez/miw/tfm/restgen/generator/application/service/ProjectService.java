package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
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
        return projectRepository.save(project);
    }

    public void deleteById(String id) {
        projectRepository.deleteById(id);
    }
}
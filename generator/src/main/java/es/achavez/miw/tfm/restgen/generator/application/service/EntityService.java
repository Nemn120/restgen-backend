package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntityService {

    @Autowired
    private ProjectRepository projectRepository;

    public EntityService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public JavaClass findByProjectIdAndClassName(String id, String className) {
        return projectRepository.findJavaClassByProjectIdAndClassName(id, className);
    }

    public List<JavaClass> findByProjectId(String id) {
        return projectRepository.findJavaClassByProjectId(id);
    }
    public JavaClass saveOrUpdate(String projectId, JavaClass javaClass) {
        return projectRepository.findById(projectId).map(project -> {
            List<JavaClass> updatedClasses = new ArrayList<>(project.getClasses().stream()
                    .map(existingClass -> {
                        if (existingClass.getName().equals(javaClass.getName())) {
                            return javaClass;
                        }
                        return existingClass;
                    }).toList());

            boolean exists = updatedClasses.stream()
                    .anyMatch(updatedClass -> updatedClass.getName().equals(javaClass.getName()));

            if (!exists) {
                updatedClasses.add(javaClass);
            }

            project.setClasses(updatedClasses);
            project.setUpdateDate(LocalDateTime.now());
            projectRepository.save(project);
            return javaClass;
        }).orElseThrow(() -> new NotFoundException("Project not found with ID: " + projectId));
    }

    public void delete(String projectId, String className) {
        this.projectRepository.findById(projectId).ifPresent(project -> {
            List<JavaClass> javaClasses = project.getClasses()
                    .stream()
                    .filter(javaClass -> !javaClass.getName().equals(className))
                    .toList();
            project.setClasses(javaClasses);
            projectRepository.save(project);
        });
    }
}

package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.in.EntityUsesCases;
import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.PlantUmlDiagramGenerator;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntityService implements EntityUsesCases {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PlantUmlDiagramGenerator plantUmlDiagramGenerator;


    @Override
    public JavaClass findByProjectIdAndClassName(String id, String className) {
        return projectRepository.findJavaClassByProjectIdAndClassName(id, className);
    }

    @Override
    public List<JavaClass> findByProjectId(String id) {
        return projectRepository.findJavaClassByProjectId(id);
    }

    @Override
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
            project.setPlantUmlDiagram(plantUmlDiagramGenerator.generateDiagram(updatedClasses));
            project.setUpdateDate(LocalDateTime.now());
            projectRepository.save(project);
            return javaClass;
        }).orElseThrow(() -> new NotFoundException("Project not found with ID: " + projectId));
    }

    @Override
    public void delete(String projectId, String className) {
        this.projectRepository.findById(projectId).ifPresent(project -> {
            List<JavaClass> javaClasses = project.getClasses()
                    .stream()
                    .filter(javaClass -> !javaClass.getName().equals(className))
                    .toList();
            project.setClasses(javaClasses);
            project.setPlantUmlDiagram(plantUmlDiagramGenerator.generateDiagram(javaClasses));
            project.setUpdateDate(LocalDateTime.now());
            projectRepository.save(project);
        });
    }
}

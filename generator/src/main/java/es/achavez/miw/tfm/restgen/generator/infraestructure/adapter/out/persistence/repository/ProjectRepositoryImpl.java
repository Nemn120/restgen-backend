package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository;

import es.achavez.miw.tfm.restgen.generator.application.port.out.ProjectRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.ProjectDocument;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.mapper.ProjectMongoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectMongoRepository mongoProjectRepository;
    private final ProjectMongoMapper projectMapper;

    @Autowired
    public ProjectRepositoryImpl(ProjectMongoRepository mongoProjectRepository, ProjectMongoMapper projectMapper) {
        this.mongoProjectRepository = mongoProjectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public Project save(Project project) {
        ProjectDocument projectDocument = projectMapper.toEntity(project);
        ProjectDocument savedEntity = mongoProjectRepository.save(projectDocument);
        return projectMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Project> findById(String id) {
        return mongoProjectRepository.findById(id)
                .map(projectMapper::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return mongoProjectRepository.findAll().stream()
                .map(projectMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoProjectRepository.deleteById(id);
    }

    @Override
    public JavaClass findJavaClassByProjectIdAndClassName(String id, String className) {
        ProjectDocument projectDocument = mongoProjectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Project not found with id: " + id));

        return projectDocument.getClasses().stream()
                .filter(javaClass -> className.equals(javaClass.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("JavaClass not found with name: " + className));
    }

    @Override
    public List<JavaClass> findJavaClassByProjectId(String id) {
        Project projectDocument = this.findById(id)
                .orElseThrow(()-> new NotFoundException("Project not found with id: " + id));
        return projectDocument != null ? projectDocument.getClasses() : null;
    }

    @Override
    public Project findDiagramPlantUmlById(String id) {
        ProjectDocument plantUmlDiagramById = this.mongoProjectRepository.findPlantUmlDiagramById(id);
        if (plantUmlDiagramById != null) {
            return projectMapper.toDomain(plantUmlDiagramById);
        }
        return null;
    }
}
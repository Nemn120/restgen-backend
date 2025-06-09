package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.mapper;

import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.ProjectDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMongoMapper {
    ProjectDocument toEntity(Project project);
    Project toDomain(ProjectDocument projectEntity);
}
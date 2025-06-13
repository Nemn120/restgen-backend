package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper;

import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.ProjectRequestDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.FindAllProjectResponseDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GetProjectResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectRestMapper {

    GetProjectResponseDTO mapToGetProjectResponse(Project project);

    Project mapCreateToDomain(ProjectRequestDTO projectDTO);

    @Mapping(target = "basePath", source = "properties.application.basePath")
    @Mapping(target = "port", source = "properties.application.port")
    FindAllProjectResponseDTO mapToFindProjectResponse(Project project);

    List<FindAllProjectResponseDTO> mapToFindProjectResponse(List<Project> projects);

}
package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper;

import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.CreateProjectRequestDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.FindAllProjectResponseDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GetProjectResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectRestMapper {

    FindAllProjectResponseDTO mapToFindProjectResponse(Project project);
    GetProjectResponseDTO mapToGetProjectResponse(Project project);
    Project mapCreateToDomain(CreateProjectRequestDTO projectDTO);
    List<FindAllProjectResponseDTO> mapToFindProjectResponse(List<Project> projects);

}
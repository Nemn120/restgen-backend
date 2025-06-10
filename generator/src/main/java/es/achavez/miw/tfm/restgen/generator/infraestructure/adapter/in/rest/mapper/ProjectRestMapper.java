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

    @Mapping(target = "properties.application.port", source = "properties.port")
    @Mapping(target = "properties.application.basePath", source = "properties.basePath")
    @Mapping(target = "properties.maven.groupId", source = "properties.groupId")
    @Mapping(target = "properties.maven.artifactId", source = "properties.artifactId")
    @Mapping(target = "properties.maven.version", source = "properties.mavenVersion")
    @Mapping(target = "properties.maven.name", source = "properties.mavenName")
    @Mapping(target = "properties.maven.description", source = "properties.mavenDescription")
    @Mapping(target = "properties.security.secretKey", source = "properties.secretKey")
    @Mapping(target = "properties.database.type", source = "properties.databaseType")
    @Mapping(target = "properties.documentation", source = "properties.documentation")
    Project mapCreateToDomain(ProjectRequestDTO projectDTO);

    FindAllProjectResponseDTO mapToFindProjectResponse(Project project);

    List<FindAllProjectResponseDTO> mapToFindProjectResponse(List<Project> projects);

}
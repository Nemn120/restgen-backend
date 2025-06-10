package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper;

import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.EntityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityRestMapper {

    @Mapping(target = "extendsClass", source = "entity.extendsClass")
    @Mapping(target = "tableName", source = "entity.tableName")
    @Mapping(target = "options", source = "entity.options")
    @Mapping(target = "columns", source = "entity.columns")
    EntityDTO mapToDTO(JavaClass javaClass);

    List<EntityDTO> mapToDTO(List<JavaClass> javaClass);

    @Mapping(source = "extendsClass", target = "entity.extendsClass")
    @Mapping(source = "tableName", target = "entity.tableName")
    @Mapping(source = "options", target = "entity.options")
    @Mapping(source = "columns", target = "entity.columns")
    JavaClass mapToDomain(EntityDTO javaClass);

    List<JavaClass> mapToDomain(List<EntityDTO> javaClass);

}

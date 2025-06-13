package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper;

import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.EntityDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.FindAllEntityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityRestMapper {

    @Mapping(target = "extendsClass", source = "entity.extendsClass")
    @Mapping(target = "tableName", source = "entity.tableName")
    FindAllEntityDTO mapToFindAllDTO(JavaClass javaClass);

    List<FindAllEntityDTO> mapToFindAllDTO(List<JavaClass> javaClass);

    EntityDTO mapToDTO(JavaClass javaClass);

    List<EntityDTO> mapToDTO(List<JavaClass> javaClass);

    JavaClass mapToDomain(EntityDTO javaClass);

    List<JavaClass> mapToDomain(List<EntityDTO> javaClass);

}

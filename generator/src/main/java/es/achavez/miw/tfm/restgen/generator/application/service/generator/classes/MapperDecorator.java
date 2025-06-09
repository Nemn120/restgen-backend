package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.GeneratorUtil;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence;
import es.achavez.miw.tfm.restgen.generator.domain.Column;
import es.achavez.miw.tfm.restgen.generator.domain.DirectoryLayerPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.*;

public class MapperDecorator<T extends JavaInterfaceSource> extends JavaClassAbstractDecorator<T> {

    public MapperDecorator(T javaClassSource, JavaClass genericJavaClass, MavenProjectPath mavenProjectPath) {
        super(javaClassSource, genericJavaClass, mavenProjectPath);
        this.directoryLayerPath = DirectoryLayerPath.MAPPER;
    }

    @Override
    public void decorate() {
        super.decorate();
        String mapperClassName = packageDirectory.getNameClassLayer(DirectoryLayerPath.MAPPER);
        String dtoClassName = packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO);
        String entityClassName = packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY);

        getJavaClassSource().setName(mapperClassName);

        addImport(DirectoryLayerPath.DTO);
        addImport(DirectoryLayerPath.ENTITY);

        addImport(MAPPER_MAPPING);
        addImport(MAPPER_MAPPINGS);
        addImport(MAPPER);

        AnnotationSource<JavaInterfaceSource> mapperAnnotation = getJavaClassSource().addAnnotation();
        mapperAnnotation.setName(AnnotationPersistence.MAPPER.getAnnotationName());
        mapperAnnotation.setStringValue("componentModel", "spring");

        getJavaClassSource().addInterface("GenericMapper<" + entityClassName + ", " + dtoClassName + ">");

        generateMapping(javaClass);
    }

    private void generateMapping(JavaClass javaClass) {
        String dtoClassName = packageDirectory.getNameClassLayer(DirectoryLayerPath.DTO);
        String entityClassName = packageDirectory.getNameClassLayer(DirectoryLayerPath.ENTITY);
        String classNameLower = GeneratorUtil.convertLowerCaseFirstLetters(javaClass.getName());

        MethodSource<JavaInterfaceSource> toDtoMethod = getJavaClassSource().addMethod();
        toDtoMethod.setName("toDto");
        toDtoMethod.setReturnType(dtoClassName);
        toDtoMethod.addParameter(entityClassName, classNameLower);
        AnnotationSource<?> mappingsAnnotation = toDtoMethod.addAnnotation("Mappings");
        StringBuilder mappingEntityTODTOBuilder = new StringBuilder("{\n" +
                   "    @Mapping(target = \"createdAt\", expression = \"java(parseToStringWithFormat("+classNameLower+".getCreatedAt()))\"),\n" +
                   "    @Mapping(target = \"updatedAt\", expression = \"java(parseToStringWithFormat("+classNameLower+".getUpdatedAt()))\"),\n" );

        List<String> mappingDTOToEntity = new ArrayList<>();
        List<String> mappingEntityToDto = new ArrayList<>();

        for (Column column : javaClass.getEntity().getColumns()) {
            if (Boolean.TRUE.equals(column.getColumn().getForeignkey())) {
                String fieldName = column.getPropertyDTO().getRelationNameWithId();
                String dtoRelationName = column.getPropertyDTO().getName();
                mappingEntityToDto.add(generateMapping(fieldName, dtoRelationName));
                mappingDTOToEntity.add(generateMapping(dtoRelationName, fieldName));

            }
        }
        appendMappings(mappingEntityTODTOBuilder, mappingEntityToDto);
        mappingEntityTODTOBuilder.append(System.lineSeparator()).append("})").append(System.lineSeparator());
        mappingsAnnotation.setLiteralValue("value", mappingEntityTODTOBuilder.toString());

        MethodSource<JavaInterfaceSource> toEntityMethod = getJavaClassSource().addMethod();
        toEntityMethod.setName("toEntity");
        toEntityMethod.setReturnType(entityClassName);
        toEntityMethod.addParameter(dtoClassName, classNameLower);
        AnnotationSource<?> mappingsAnnotationToEntity = toEntityMethod.addAnnotation("Mappings");

        StringBuilder mappingDTOToEntityBuilder = new StringBuilder("{\n" +
                                                                    "    @Mapping(target = \"createdAt\", expression = \"java(parseToLocalDateTime("+classNameLower+".getCreatedAt()))\"),\n" +
                                                                    "    @Mapping(target = \"updatedAt\", expression = \"java(parseToLocalDateTime("+classNameLower+".getUpdatedAt()))\"),\n" );
        appendMappings(mappingDTOToEntityBuilder, mappingDTOToEntity);
        mappingDTOToEntityBuilder.append(System.lineSeparator()).append("})").append(System.lineSeparator());
        mappingsAnnotationToEntity.setLiteralValue("value", mappingDTOToEntityBuilder.toString());
    }

    private String generateMapping(String source, String target) {
        return "@Mapping(source = \"" + source + "\", target = \"" + target + "\")";
    }

    private void appendMappings(StringBuilder result, List<String> mappings) {
        for (int i = 0; i < mappings.size(); i++) {
            result.append("    ").append(mappings.get(i));
            if (i < mappings.size() - 1) {
                result.append(",").append(System.lineSeparator());
            }
        }
    }
}
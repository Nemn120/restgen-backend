package es.achavez.miw.tfm.restgen.generator.domain;
public enum AnnotationPersistence {
    COLUMN("Column", "javax.persistence.Column"),
    DISCRIMINATOR_COLUMN("DiscriminatorColumn", "javax.persistence.DiscriminatorColumn"),
    DISCRIMINATOR_VALUE("DiscriminatorValue", "javax.persistence.DiscriminatorValue"),
    DISCRIMINATOR_TYPE("DiscriminatorType", "javax.persistence.DiscriminatorType"),
    DISCRIMINATOR_OPTIONS("DiscriminatorOptions", "org.hibernate.annotations.DiscriminatorOptions"),
    ENTITY("Entity", "javax.persistence.Entity"),
    ENUM_TYPE("EnumType", "javax.persistence.EnumType"),
    ENUMERATED("Enumerated", "javax.persistence.Enumerated"),
    FETCH_TYPE("FetchType", "javax.persistence.FetchType"),
    GENERATED_VALUE("GeneratedValue", "javax.persistence.GeneratedValue"),
    GENERATION_TYPE("GenerationType", "javax.persistence.GenerationType"),
    ID("Id", "javax.persistence.Id"),
    INHERITANCE("Inheritance", "javax.persistence.Inheritance"),
    INHERITANCE_TYPE("InheritanceType", "javax.persistence.InheritanceType"),
    JOIN_COLUMN("JoinColumn", "javax.persistence.JoinColumn"),
    MANY_TO_ONE("ManyToOne", "javax.persistence.ManyToOne"),
    SEQUENCE_GENERATOR("SequenceGenerator", "javax.persistence.SequenceGenerator"),
    TABLE("Table", "javax.persistence.Table"),
    TEMPORAL("Temporal", "javax.persistence.Temporal"),
    TEMPORAL_TYPE("TemporalType", "javax.persistence.TemporalType"),
    UNIQUE_CONSTRAINT("UniqueConstraint", "javax.persistence.UniqueConstraint"),

    AUDITED_ENVERS("Audited", "org.hibernate.envers.Audited"),
    RELATION_TARGET_AUDITED_MODE_ENVERS("RelationTargetAuditMode", "org.hibernate.envers.RelationTargetAuditMode"),
    RELATION_TARGET_AUDITED_MODE_NOTAUDITED_ENVERS("RelationTargetAuditMode.NOT_AUDITED", "org.hibernate.envers.RelationTargetAuditMode"),

    AUDITABLE_ENTITY("AuditableEntity", ".entity.AuditableEntity"),
    ABSTRACT_ID_ENTITY("AbstractIdEntity", ".entity.AbstractIdEntity"),

    LOMBOK_GETTER("Getter", "lombok.Getter"),
    LOMBOK_SETTER("Setter", "lombok.Setter"),
    GENERIC_REPOSITORY("GenericRepository", ".repository.GenericRepository"),
    SERVICE("Service", "org.springframework.stereotype.Service"),
    TRANSACTIONAL("Transactional", "org.springframework.transaction.annotation.Transactional"),
    GENERIC_SERVICE_IMPL("GenericServiceImpl", ".service.impl.GenericServiceImpl"),
    GENERIC_SERVICE("GenericService", ".service.GenericService"),

    REST_CONTROLLER("RestController", "org.springframework.web.bind.annotation.RestController"),
    REQUEST_MAPPING("RequestMapping", "org.springframework.web.bind.annotation.RequestMapping"),
    AUTOWIRED("Autowired", "org.springframework.beans.factory.annotation.Autowired"),
    GET_MAPPING("GetMapping", "org.springframework.web.bind.annotation.GetMapping"),
    POST_MAPPING("PostMapping", "org.springframework.web.bind.annotation.PostMapping"),
    PUT_MAPPING("PutMapping", "org.springframework.web.bind.annotation.PutMapping"),
    DELETE_MAPPING("DeleteMapping", "org.springframework.web.bind.annotation.DeleteMapping"),

    OPERATION("Operation", "io.swagger.v3.oas.annotations.Operation"),
    SECURITY_REQUIREMENT("SecurityRequirement", "io.swagger.v3.oas.annotations.security.SecurityRequirement"),
    SORT("Sort", "org.springframework.data.domain.Sort"),
    SORT_DIRECTION("Sort.Direction", "org.springframework.data.domain.Sort.Direction"),
    BIN_ANNOTATION("bin_annotation", "org.springframework.web.bind.annotation.*"),


    RESPONSE_ENTITY_UTIL("ResponseEntityUtil", ".util.response.ResponseEntityUtil"),
    RESPONSE_CUSTOM_PAGE("CustomPage", ".util.response.CustomPage"),
    GENERIC_RESPONSE("GenericResponse", ".util.response.GenericResponse"),

    HTTP_STATUS("HttpStatus", "org.springframework.http.HttpStatus"),
    RESPONSE_ENTITY("ResponseEntity", "org.springframework.http.ResponseEntity"),
    LOGGER("Logger", "org.slf4j.Logger"),
    LOGGER_FACTORY("LoggerFactory", "org.slf4j.LoggerFactory"),
    LIST("List", "java.util.List"),
    GENERIC_MAPPER("GenericMapper",".mapper.GenericMapper"),
    MAPPER("Mapper","org.mapstruct.Mapper" ),
    MAPPER_MAPPING("Mapping","org.mapstruct.Mapping" ),
    MAPPER_MAPPINGS("Mappings","org.mapstruct.Mappings"),
    AUDITABLE_DTO("AuditableDTO",".dto.AuditableDTO");

    private final String annotationName;
    private final String packageName;

    AnnotationPersistence(String annotationName, String packageName) {
        this.annotationName = annotationName;
        this.packageName = packageName;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public String getPackageName() {
        return packageName;
    }
}

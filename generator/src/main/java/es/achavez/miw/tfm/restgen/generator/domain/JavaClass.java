package es.achavez.miw.tfm.restgen.generator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JavaClass {

    private String name;
    private String apiName;
    private EntityClass entity;

    public JavaClass() {
    }

    public JavaClass(String name, EntityClass entityClass) {
        Validate.notBlank(name);
        Validate.notNull(entityClass, "entidad es necesario");

        this.name = name;
        this.entity = entityClass;
        if(StringUtils.isBlank(entityClass.getTableName())){
            this.entity.setTableName(name.toUpperCase());
        }
    }
}

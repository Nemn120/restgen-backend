package es.achavez.miw.tfm.restgen.generator.application.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class ObjectMapperYAML {
    ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private static ObjectMapperYAML objectMapperJSON;

    public static ObjectMapperYAML getInstance(){
        if(objectMapperJSON == null){
            objectMapperJSON = new ObjectMapperYAML();
        }
        return objectMapperJSON;
    }

    public <T> T readObjectByPath(String path,Class<T> object) throws IOException {
        return objectMapper.readValue(new File(path),object);
    }

    public <T> T readObjectByString(String object,Class<T> objectTarget) throws IOException {
        return objectMapper.readValue(object,objectTarget);
    }

    public void writeObject(String path,Object object) throws IOException {
        objectMapper.writeValue(new File(path),object);
    }
}

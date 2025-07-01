package es.achavez.miw.tfm.restgen.generator.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ObjectMapperJSON {
    ObjectMapper objectMapper = new ObjectMapper();

    private static ObjectMapperJSON objectMapperJSON;

    public static ObjectMapperJSON getInstance(){
        if(objectMapperJSON == null){
            objectMapperJSON = new ObjectMapperJSON();
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

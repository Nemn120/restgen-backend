package es.achavez.miw.tfm.restgen.generator.application.port.in;

import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;

import java.util.List;

public interface EntityUsesCases {

    JavaClass findByProjectIdAndClassName(String id, String className);

    List<JavaClass> findByProjectId(String id);

    JavaClass saveOrUpdate(String projectId, JavaClass javaClass);

    void delete(String projectId, String className);
}

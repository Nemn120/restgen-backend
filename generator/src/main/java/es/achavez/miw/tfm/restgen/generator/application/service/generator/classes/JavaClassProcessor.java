package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import org.jboss.forge.roaster.model.source.JavaSource;

public interface JavaClassProcessor<T extends JavaSource> {

    void decorate();

    String printClass();

    T getJavaClassSource();

}

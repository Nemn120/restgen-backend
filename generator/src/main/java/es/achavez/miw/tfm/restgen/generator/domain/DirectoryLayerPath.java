package es.achavez.miw.tfm.restgen.generator.domain;

public enum DirectoryLayerPath {

    CONTROLLER(".controller" , "Controller"),
    CONTROLLER_IMPL(".controller.impl" , "ControllerImpl"),
    SERVICE(".service", "Service"),
    SERVICE_IMPL(".service.impl", "ServiceImpl"),
    REPOSITORY(".repository", "Repository"),
    ENTITY(".entity", ""),
    DTO(".dto", "DTO"),
    MAPPER(".mapper", "Mapper");

    String subPath;
    String className;

    DirectoryLayerPath(String s, String className) {
        this.subPath = s;
        this.className = className;
    }

    public String getSubPath() {
        return subPath;
    }

    public String getClassName() {
        return className;
    }
}

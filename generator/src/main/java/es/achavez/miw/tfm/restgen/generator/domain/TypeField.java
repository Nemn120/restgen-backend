package es.achavez.miw.tfm.restgen.generator.domain;

public enum TypeField {

    STRING("String"),
    INTEGER("Integer"),
    LONG("Long"),
    DOUBLE("Double"),
    BOOLEAN("Boolean"),
    DATE("Date"),
    ENUM("Enum"),
    OBJECT("Object");

    private final String type;

    TypeField(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

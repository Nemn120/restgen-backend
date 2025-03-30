package es.fchavez.miw.tfm.restgen.generator.models;

public enum Role {
    ADMIN, DEVELOPER, ANONYMOUS;

    public static final String PREFIX = "ROLE_";

    public static Role of(String withPrefix) {
        return Role.valueOf(withPrefix.replace(Role.PREFIX, ""));
    }

}

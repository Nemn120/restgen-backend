package es.achavez.miw.tfm.restgen.generator.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubRepository {

    private String url;
    private String userName;
    private String name;
    private String description;
    private Boolean isPrivate;
}

package es.achavez.miw.tfm.restgen.generator.application.port.in;

import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.UserDto;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.UserInfoDocument;

public interface UserUsesCases {

    String login(String email);

    void createUser(UserInfoDocument user);

    UserInfoDocument findByEmailAssured(String email);

    UserDto getUserGithub(String code, String state);

}

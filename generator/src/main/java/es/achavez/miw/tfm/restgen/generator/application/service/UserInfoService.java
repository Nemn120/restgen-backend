package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.UserDto;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.Role;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.UserInfo;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository.UserInfoRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.ConflictException;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.security.GitHubOAuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserInfoService {

    private final UserInfoRepository repository;

    private final JwtService jwtService;

    private final GitHubOAuthService gitHubOAuthService;

    public UserInfoService(UserInfoRepository repository, JwtService jwtService, GitHubOAuthService gitHubOAuthService) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.gitHubOAuthService = gitHubOAuthService;
    }

    public String login(String email) {
        return this.repository.findByEmail(email)
                .map(user -> jwtService.createToken(user.getEmail(), user.getName(), user.getRole().name()))
                .orElseThrow(() -> new NotFoundException("Impossible, you should have already logged in."));
    }

    public void createUser(UserInfo user) {
        this.assertNoExistByEmail(user.getEmail());
        user.setCreateDate(LocalDateTime.now());
        this.repository.save(user);
    }

    private void assertNoExistByEmail(String email) {
        if (this.repository.findByEmail(email).isPresent()) {
            throw new ConflictException("The email already exists: " + email);
        }
    }

    public UserInfo findByEmailAssured(String email) {
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The email don't exist: " + email));
    }

    public UserDto getUserGithub(String code, String state){

        Map<String, Object> userInfo = gitHubOAuthService.getUserInfo(code, state);
        String email = userInfo.get("email").toString();
        String name = userInfo.get("name").toString();
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setRole(Role.DEVELOPER);
        System.out.println(userInfo.get("created_at"));
        //userDto.setCreateDate();
        userDto.setName(name);
        userDto.setToken(jwtService.createToken(email, email, Role.DEVELOPER.name()));
        return userDto;
    }

}

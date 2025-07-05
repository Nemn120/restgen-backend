package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.in.JwtUsesCases;
import es.achavez.miw.tfm.restgen.generator.application.port.in.UserUsesCases;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.UserDto;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.Role;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.UserInfoDocument;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository.UserInfoRepository;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.ConflictException;
import es.achavez.miw.tfm.restgen.generator.application.service.exceptions.NotFoundException;
import es.achavez.miw.tfm.restgen.generator.security.GitHubOAuthService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserUsesCases {

    private final UserInfoRepository repository;

    private final JwtUsesCases jwtUsesCases;

    private final GitHubOAuthService gitHubOAuthService;

    public UserService(UserInfoRepository repository, JwtUsesCases jwtUsesCases, GitHubOAuthService gitHubOAuthService) {
        this.repository = repository;
        this.jwtUsesCases = jwtUsesCases;
        this.gitHubOAuthService = gitHubOAuthService;
    }

    @Override
    public String login(String email) {
        return this.repository.findByEmail(email)
                .map(user -> jwtUsesCases.createToken(user.getEmail(), user.getName(), user.getRole().name(), null))
                .orElseThrow(() -> new NotFoundException("Impossible, you should have already logged in."));
    }

    @Override
    public void createUser(UserInfoDocument user) {
        this.assertNoExistByEmail(user.getEmail());
        user.setCreateDate(LocalDateTime.now());
        this.repository.save(user);
    }

    @Override
    public UserInfoDocument findByEmailAssured(String email) {
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The email don't exist: " + email));
    }

    @Override
    public UserDto getUserGithub(String code, String state) {

        Map<String, Object> userInfo = gitHubOAuthService.getUserInfo(code, state);
        String email;
        if(userInfo.get("email") != null && !userInfo.get("email").toString().isEmpty()) {
            email = (String) userInfo.get("email");
        } else {
            email = userInfo.get("login").toString();
        }
        String name = userInfo.get("name").toString();
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setRole(Role.DEVELOPER);
        String tokenGithub = userInfo.get("access_token").toString();
        this.verifyTokenScopes(tokenGithub);
        userDto.setName(name);
        userDto.setToken(jwtUsesCases.createToken(email, name, Role.DEVELOPER.name(), tokenGithub));
        return userDto;
    }

    void assertNoExistByEmail(String email) {
        if (this.repository.findByEmail(email).isPresent()) {
            throw new ConflictException("The email already exists: " + email);
        }
    }

    void verifyTokenScopes(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.github.com/user", HttpMethod.GET, request, String.class
        );

        List<String> scopes = response.getHeaders().get("X-OAuth-Scopes");
        if (scopes == null || !scopes.contains("repo")) {
            throw new RuntimeException("El token no tiene el scope 'repo'.");
        }
    }

}

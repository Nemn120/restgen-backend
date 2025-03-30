package es.fchavez.miw.tfm.restgen.generator.service;

import es.fchavez.miw.tfm.restgen.generator.models.Role;
import es.fchavez.miw.tfm.restgen.generator.models.UserInfo;
import es.fchavez.miw.tfm.restgen.generator.repository.UserInfoRepository;
import es.fchavez.miw.tfm.restgen.generator.service.exceptions.ConflictException;
import es.fchavez.miw.tfm.restgen.generator.service.exceptions.ForbiddenException;
import es.fchavez.miw.tfm.restgen.generator.service.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserInfoService {

    private final UserInfoRepository repository;

    private final JwtService jwtService;

    public UserInfoService(UserInfoRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public String login(String email) {
        return this.repository.findByEmail(email)
                .map(user -> jwtService.createToken(user.getEmail(), user.getName(), user.getRole().name()))
                .orElseThrow(() -> new NotFoundException("Impossible, you should have already logged in."));
    }

    public void createUser(UserInfo user, Role roleClaim) {
        if (!authorizedRoles(roleClaim).contains(user.getRole())) {
            throw new ForbiddenException("Insufficient role to create this user: " + user);
        }
        this.assertNoExistByEmail(user.getEmail());
        user.setCreateDate(LocalDateTime.now());
        this.repository.save(user);
    }

    private void assertNoExistByEmail(String mobile) {
        if (this.repository.findByEmail(mobile).isPresent()) {
            throw new ConflictException("The mobile already exists: " + mobile);
        }
    }

    private List<Role> authorizedRoles(Role roleClaim) {
        if (Role.ADMIN.equals(roleClaim)) {
            return List.of(Role.ADMIN, Role.DEVELOPER);
        } else if (Role.DEVELOPER.equals(roleClaim)) {
            return List.of(Role.DEVELOPER);
        } else {
            return List.of();
        }
    }

    public UserInfo findByEmailAssured(String email) {
        return this.repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The mobile don't exist: " + email));
    }

}

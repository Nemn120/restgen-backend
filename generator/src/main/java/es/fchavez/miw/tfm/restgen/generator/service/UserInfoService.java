package es.fchavez.miw.tfm.restgen.generator.service;

import es.fchavez.miw.tfm.restgen.generator.models.UserInfo;
import es.fchavez.miw.tfm.restgen.generator.repository.UserInfoRepository;
import es.fchavez.miw.tfm.restgen.generator.service.exceptions.ConflictException;
import es.fchavez.miw.tfm.restgen.generator.service.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

}

package es.fchavez.miw.tfm.restgen.generator.controller.rest;

import es.fchavez.miw.tfm.restgen.generator.controller.dto.TokenDto;
import es.fchavez.miw.tfm.restgen.generator.controller.dto.UserDto;
import es.fchavez.miw.tfm.restgen.generator.models.Role;
import es.fchavez.miw.tfm.restgen.generator.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController {

    public static final String EMAIL = "/{email}";
    public static final String TOKEN = "/token";

    @Autowired
    private UserInfoService service;

    @PostMapping(value = TOKEN)
    public TokenDto login(@AuthenticationPrincipal User activeUser) {
        TokenDto token = new TokenDto(service.login(activeUser.getUsername()));
        log.debug(token::toString);
        return token;
    }

    @PostMapping
    public void createUser(@Valid @RequestBody UserDto creationUserDto) {
        this.service.createUser(creationUserDto.toUser(), this.extractRoleClaims());
    }

    @GetMapping(EMAIL)
    public UserDto readUser(@PathVariable String email) {
        return new UserDto(this.service.findByEmailAssured(email));
    }

    private Role extractRoleClaims() {
        List<String> roleClaims = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        return Role.of(roleClaims.getFirst());
    }
}

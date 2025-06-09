package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.controller;

import es.achavez.miw.tfm.restgen.generator.application.service.UserInfoService;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.TokenDto;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.UserDto;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/users")
public class UserController {

    public static final String EMAIL = "/{email}";
    public static final String TOKEN = "/token";

    @Autowired
    private UserInfoService service;

    @PreAuthorize("authenticated")
    @PostMapping(value = TOKEN)
    public TokenDto login(@AuthenticationPrincipal User activeUser) {
        TokenDto token = new TokenDto(service.login(activeUser.getUsername()));
        log.debug(token::toString);
        return token;
    }

    @PostMapping
    public void createUser(@Valid @RequestBody UserDto creationUserDto) {
        this.service.createUser(creationUserDto.toUser());
    }

    @GetMapping(EMAIL)
    public UserDto readUser(@PathVariable String email) {
        return new UserDto(this.service.findByEmailAssured(email));
    }
}

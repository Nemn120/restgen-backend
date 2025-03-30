package es.fchavez.miw.tfm.restgen.generator.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.fchavez.miw.tfm.restgen.generator.models.Role;
import es.fchavez.miw.tfm.restgen.generator.models.UserInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String name;
    private String password;
    private Role role;
    private LocalDateTime createDate;

    public UserDto(UserInfo user) {
        BeanUtils.copyProperties(user, this);
        this.password = "secret";
    }

    public void doDefault() {
        if (Objects.isNull(password)) {
            password = UUID.randomUUID().toString();
        }
        if (Objects.isNull(role)) {
            this.role = Role.DEVELOPER;
        }
    }

    public UserInfo toUser() {
        this.doDefault();
        UserInfo user = new UserInfo();
        BeanUtils.copyProperties(this, user);
        user.setPassword(new BCryptPasswordEncoder().encode(this.password));
        return user;
    }
}

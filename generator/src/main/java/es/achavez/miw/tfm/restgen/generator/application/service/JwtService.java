package es.achavez.miw.tfm.restgen.generator.application.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.achavez.miw.tfm.restgen.generator.application.port.in.JwtUsesCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtService implements JwtUsesCases {
    private static final String BEARER = "Bearer ";
    private static final int PARTIES = 3;
    private static final String USER_CLAIM = "user";
    private static final String NAME_CLAIM = "name";
    private static final String ROLE_CLAIM = "role";
    private static final String TOKEN_GITHUB = "token-github";

    private final String secret;
    private final String issuer;
    private final int expire;

    @Autowired
    public JwtService(@Value("${restgen.jwt.secret}") String secret, @Value("${restgen.jwt.issuer}") String issuer,
                      @Value("${restgen.jwt.expire}") int expire) {
        this.secret = secret;
        this.issuer = issuer;
        this.expire = expire;
    }

    @Override
    public String extractToken(String bearer) {
        if (bearer != null && bearer.startsWith(BEARER) && PARTIES == bearer.split("\\.").length) {
            return bearer.substring(BEARER.length());
        } else {
            return "";
        }
    }

    @Override
    public String createToken(String user, String name, String role, String tokeGithub) {
        return JWT.create()
                .withIssuer(this.issuer)
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expire * 1000L))
                .withClaim(USER_CLAIM, user)
                .withClaim(NAME_CLAIM, name)
                .withClaim(ROLE_CLAIM, role)
                .withClaim(TOKEN_GITHUB, tokeGithub)
                .sign(Algorithm.HMAC256(this.secret));

    }

    @Override
    public String user(String authorization) {
        return this.verify(authorization)
                .map(jwt -> jwt.getClaim(USER_CLAIM).asString())
                .orElse("");
    }

    @Override
    public String name(String authorization) {
        return this.verify(authorization)
                .map(jwt -> jwt.getClaim(NAME_CLAIM).asString())
                .orElse("");
    }

    @Override
    public String role(String authorization) {
        return this.verify(authorization)
                .map(jwt -> jwt.getClaim(ROLE_CLAIM).asString())
                .orElse("");
    }

    private Optional<DecodedJWT> verify(String token) {
        try {
            return Optional.of(JWT.require(Algorithm.HMAC256(this.secret))
                    .withIssuer(this.issuer).build()
                    .verify(token));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

}
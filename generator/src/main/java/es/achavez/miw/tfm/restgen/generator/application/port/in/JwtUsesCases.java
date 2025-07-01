package es.achavez.miw.tfm.restgen.generator.application.port.in;

public interface JwtUsesCases {
    String extractToken(String bearer);

    String createToken(String user, String name, String role, String tokeGithub);

    String user(String authorization);

    String name(String authorization);

    String role(String authorization);
}

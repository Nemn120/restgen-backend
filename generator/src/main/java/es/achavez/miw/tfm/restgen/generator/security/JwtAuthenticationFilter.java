package es.achavez.miw.tfm.restgen.generator.security;

import es.achavez.miw.tfm.restgen.generator.application.port.in.JwtUsesCases;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtUsesCases jwtUsesCases;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws IOException, ServletException {

        String method = request.getMethod();
        String requestUrl = request.getRequestURL().toString();
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        logger.info("Solicitud recibida: Método={}, URL={}", method, requestUrl);
        if (authorizationHeader != null) {
            logger.info("Encabezado Authorization: {}", authorizationHeader);
        } else {
            logger.info("Encabezado Authorization no presente.");
        }

        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = jwtUsesCases.extractToken(request.getHeader(AUTHORIZATION));
            if (!token.isEmpty()) {
                GrantedAuthority authority = new SimpleGrantedAuthority(Role.PREFIX + jwtUsesCases.role(token));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(jwtUsesCases.user(token), null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}
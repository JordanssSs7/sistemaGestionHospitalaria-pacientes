package com.tecsup.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String MEDICO = "MEDICO";
    private static final String RECEPCIONISTA = "RECEPCIONISTA";

    // Cifra las contraseñas de los usuarios con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // La app usa sesion por cookie y fetch del mismo origen; se deja CSRF desactivado como en P3
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Recursos publicos: login, pagina de sin acceso y archivos estaticos
                        .requestMatchers("/login.html", "/sin-acceso.html", "/css/**", "/js/**", "/error").permitAll()

                        // Solo ADMINISTRADOR: usuarios, roles y bitacora (API y pantallas)
                        .requestMatchers("/api/usuarios/**", "/api/roles/**", "/api/auditoria/**").hasRole(ADMIN)
                        .requestMatchers("/usuarios.html", "/roles.html", "/auditoria.html").hasRole(ADMIN)

                        // Eliminar contactos y alergias: RECEPCIONISTA no puede
                        .requestMatchers(HttpMethod.DELETE, "/api/pacientes/*/contactos/**", "/api/alergias/**")
                                .hasAnyRole(ADMIN, MEDICO)

                        // Citas: ADMINISTRADOR y RECEPCIONISTA (modulo de citas, si se implementa)
                        .requestMatchers("/api/citas/**", "/citas.html").hasAnyRole(ADMIN, RECEPCIONISTA)

                        // Pacientes: los tres roles
                        .requestMatchers("/api/pacientes/**", "/api/alergias/**")
                                .hasAnyRole(ADMIN, MEDICO, RECEPCIONISTA)

                        // Todo lo demas (paginas de pacientes, /api/me) exige estar autenticado
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login.html?error")
                        // Redireccion despues del login segun el rol
                        .successHandler((request, response, authentication) -> {
                            boolean esAdmin = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .anyMatch(("ROLE_" + ADMIN)::equals);
                            response.sendRedirect(esAdmin ? "/usuarios.html" : "/index.html");
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html"))
                .exceptionHandling(ex -> ex
                        // Sin sesion: la API responde 401 y las paginas llevan al login
                        .authenticationEntryPoint((request, response, e) -> {
                            if (esApi(request)) {
                                responderJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Debes iniciar sesión para continuar.");
                            } else {
                                response.sendRedirect("/login.html");
                            }
                        })
                        // Con sesion pero sin permiso: la API responde 403 y las paginas llevan a sin-acceso
                        .accessDeniedHandler((request, response, e) -> {
                            if (esApi(request)) {
                                responderJson(response, HttpServletResponse.SC_FORBIDDEN,
                                        "No tienes permiso para realizar esta acción.");
                            } else {
                                response.sendRedirect("/sin-acceso.html");
                            }
                        }));
        return http.build();
    }

    private boolean esApi(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    private void responderJson(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}

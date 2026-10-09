package com.tecsup.config;

import com.tecsup.model.Rol;
import com.tecsup.model.Usuario;
import com.tecsup.repository.RolRepository;
import com.tecsup.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Al iniciar la aplicación crea los roles base y, si no hay usuarios, un administrador inicial
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.nombre:Administrador del sistema}")
    private String adminNombre;

    @Autowired
    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol admin = crearRolSiNoExiste("ADMINISTRADOR", "Acceso a usuarios, roles y módulos del sistema");
        crearRolSiNoExiste("MEDICO", "Acceso a pacientes e historias clínicas");
        crearRolSiNoExiste("RECEPCIONISTA", "Acceso a pacientes y citas");

        if (usuarioRepository.count() == 0) {
            Usuario usuario = new Usuario();
            usuario.setUsername(adminUsername);
            usuario.setPassword(passwordEncoder.encode(adminPassword));
            usuario.setNombreCompleto(adminNombre);
            usuario.setRol(admin);
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
        }
    }

    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rol.setDescripcion(descripcion);
            rol.setActivo(true);
            return rolRepository.save(rol);
        });
    }
}

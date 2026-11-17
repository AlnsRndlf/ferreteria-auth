package cl.duocuc.ferreteria_auth.config;

import cl.duocuc.ferreteria_auth.model.Usuario;
import cl.duocuc.ferreteria_auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Crea un usuario de cada rol la primera vez que se enciende (si no existen).
// Los correos usan los dominios que acepta el formulario del frontend (@duoc.cl).
@Configuration
public class DatosIniciales {

  @Bean
  CommandLineRunner usuariosIniciales(UsuarioRepository repo, PasswordEncoder encoder) {
    return args -> {
      crear(repo, encoder, "Administrador", "admin@duoc.cl", "Admin123", "ADMIN");
      crear(repo, encoder, "Vendedor", "vendedor@duoc.cl", "Vend1234", "VENDEDOR");
      crear(repo, encoder, "Cliente", "cliente@duoc.cl", "Cliente1", "CLIENTE");
    };
  }

  private void crear(UsuarioRepository repo, PasswordEncoder encoder,
      String nombre, String correo, String clave, String rol) {
    if (repo.findByCorreoIgnoreCase(correo).isEmpty()) {
      Usuario u = new Usuario();
      u.setNombre(nombre);
      u.setCorreo(correo);
      u.setPassword(encoder.encode(clave));
      u.setRol(rol);
      repo.save(u);
    }
  }
}

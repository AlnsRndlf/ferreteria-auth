package cl.duocuc.ferreteria_auth.config;

import cl.duocuc.ferreteria_auth.model.Usuario;
import cl.duocuc.ferreteria_auth.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Deja un usuario de cada rol. Si ya existían (de pasos anteriores) y les faltan
// datos (RUN, apellidos, dirección), se los completa.
@Configuration
public class DatosIniciales {

  @Bean
  CommandLineRunner usuariosIniciales(UsuarioRepository repo, PasswordEncoder encoder) {
    return args -> {
      asegurar(repo, encoder, "111111111", "Administrador", "Ferretería", "admin@duoc.cl", "Admin123", "ADMIN");
      asegurar(repo, encoder, "222222222", "Vendedor", "Ferretería", "vendedor@duoc.cl", "Vend1234", "VENDEDOR");
      asegurar(repo, encoder, "333333333", "Cliente", "Demostración", "cliente@duoc.cl", "Cliente1", "CLIENTE");
    };
  }

  private void asegurar(UsuarioRepository repo, PasswordEncoder encoder, String run,
      String nombre, String apellidos, String correo, String clave, String rol) {
    Usuario u = repo.findByCorreoIgnoreCase(correo).orElseGet(Usuario::new);

    if (u.getId() == null) {
      u.setNombre(nombre);
      u.setCorreo(correo);
      u.setPassword(encoder.encode(clave));
      u.setRol(rol);
    }

    if (u.getRun() == null && !repo.existsByRun(run)) {
      u.setRun(run);
      u.setApellidos(apellidos);
      u.setRegion("Región Metropolitana de Santiago");
      u.setComuna("Santiago");
      u.setDireccion("Av. Libertador Bernardo O'Higgins 1000");
    }

    repo.save(u);
  }
}

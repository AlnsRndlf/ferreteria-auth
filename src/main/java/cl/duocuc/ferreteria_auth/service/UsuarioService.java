package cl.duocuc.ferreteria_auth.service;

import cl.duocuc.ferreteria_auth.dto.UsuarioRequest;
import cl.duocuc.ferreteria_auth.dto.UsuarioResponse;
import cl.duocuc.ferreteria_auth.exception.DatosInvalidosException;
import cl.duocuc.ferreteria_auth.exception.RecursoNoEncontradoException;
import cl.duocuc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duocuc.ferreteria_auth.model.Usuario;
import cl.duocuc.ferreteria_auth.repository.UsuarioRepository;
import cl.duocuc.ferreteria_auth.validation.ValidadorRun;
import java.util.List;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Gestión de usuarios: solo el ADMIN (lo exige SecurityConfig)
@Service
public class UsuarioService {

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;

  public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) {
    this.usuarios = usuarios;
    this.encoder = encoder;
  }

  public List<UsuarioResponse> listar() {
    return usuarios.findAll().stream().map(this::aRespuesta).toList();
  }

  public UsuarioResponse buscar(Long id) {
    return aRespuesta(buscarEntidad(id));
  }

  // Los datos del usuario que inició sesión (para "Mi perfil")
  public UsuarioResponse perfil(String correo) {
    return aRespuesta(usuarios.findByCorreoIgnoreCase(correo)
        .orElseThrow(() -> new BadCredentialsException("Usuario no existe")));
  }

  public UsuarioResponse crear(UsuarioRequest datos) {
    if (datos.password() == null || datos.password().isBlank()) {
      throw new DatosInvalidosException("password: la contraseña es obligatoria");
    }
    validarRun(datos.run());
    String run = datos.run().trim().toUpperCase();
    String correo = datos.correo().trim().toLowerCase();

    if (usuarios.existsByCorreoIgnoreCase(correo)) {
      throw new ReglaNegocioException("Ya existe un usuario con el correo: " + correo);
    }
    if (usuarios.existsByRun(run)) {
      throw new ReglaNegocioException("Ya existe un usuario con el RUN: " + run);
    }

    Usuario usuario = new Usuario();
    copiarDatos(usuario, datos, run, correo);
    usuario.setPassword(encoder.encode(datos.password()));
    return aRespuesta(usuarios.save(usuario));
  }

  public UsuarioResponse actualizar(Long id, UsuarioRequest datos) {
    Usuario usuario = buscarEntidad(id);
    validarRun(datos.run());
    String run = datos.run().trim().toUpperCase();
    String correo = datos.correo().trim().toLowerCase();

    if (usuarios.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
      throw new ReglaNegocioException("Ya existe un usuario con el correo: " + correo);
    }
    if (usuarios.existsByRunAndIdNot(run, id)) {
      throw new ReglaNegocioException("Ya existe un usuario con el RUN: " + run);
    }

    copiarDatos(usuario, datos, run, correo);
    // Si no se envía contraseña, se conserva la actual
    if (datos.password() != null && !datos.password().isBlank()) {
      usuario.setPassword(encoder.encode(datos.password()));
    }
    return aRespuesta(usuarios.save(usuario));
  }

  public void eliminar(Long id, String correoActual) {
    Usuario usuario = buscarEntidad(id);
    // Evita que el administrador se quede sin acceso borrando su propia cuenta
    if (usuario.getCorreo().equalsIgnoreCase(correoActual)) {
      throw new ReglaNegocioException("No puedes eliminar tu propia cuenta");
    }
    usuarios.delete(usuario);
  }

  private Usuario buscarEntidad(Long id) {
    return usuarios.findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
  }

  private void validarRun(String run) {
    if (!ValidadorRun.esValido(run)) {
      throw new DatosInvalidosException("run: RUN inválido (dígito verificador incorrecto)");
    }
  }

  private void copiarDatos(Usuario usuario, UsuarioRequest datos, String run, String correo) {
    usuario.setRun(run);
    usuario.setNombre(datos.nombre().trim());
    usuario.setApellidos(datos.apellidos().trim());
    usuario.setCorreo(correo);
    usuario.setRegion(datos.region());
    usuario.setComuna(datos.comuna());
    usuario.setDireccion(datos.direccion().trim());
    usuario.setRol(datos.rol());
  }

  private UsuarioResponse aRespuesta(Usuario u) {
    return new UsuarioResponse(u.getId(), u.getRun(), u.getNombre(), u.getApellidos(),
        u.getCorreo(), u.getRegion(), u.getComuna(), u.getDireccion(), u.getRol());
  }
}

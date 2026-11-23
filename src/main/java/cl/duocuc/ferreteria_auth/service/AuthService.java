package cl.duocuc.ferreteria_auth.service;

import cl.duocuc.ferreteria_auth.dto.LoginRequest;
import cl.duocuc.ferreteria_auth.dto.LoginResponse;
import cl.duocuc.ferreteria_auth.dto.RegistroRequest;
import cl.duocuc.ferreteria_auth.exception.DatosInvalidosException;
import cl.duocuc.ferreteria_auth.exception.ReglaNegocioException;
import cl.duocuc.ferreteria_auth.model.Usuario;
import cl.duocuc.ferreteria_auth.repository.UsuarioRepository;
import cl.duocuc.ferreteria_auth.security.JwtService;
import cl.duocuc.ferreteria_auth.validation.ValidadorRun;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt) {
    this.usuarios = usuarios;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  public LoginResponse login(LoginRequest datos) {
    Usuario usuario = usuarios.findByCorreoIgnoreCase(datos.correo())
        .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
    // Mismo mensaje si falla el correo o la clave: no damos pistas a un atacante
    if (!encoder.matches(datos.password(), usuario.getPassword())) {
      throw new BadCredentialsException("Credenciales inválidas");
    }
    return respuesta(usuario);
  }

  // Quien se registra desde la tienda SIEMPRE queda como CLIENTE
  public LoginResponse registrar(RegistroRequest datos) {
    if (!ValidadorRun.esValido(datos.run())) {
      throw new DatosInvalidosException("run: RUN inválido (dígito verificador incorrecto)");
    }
    String run = datos.run().trim().toUpperCase();
    String correo = datos.correo().trim().toLowerCase();

    if (usuarios.existsByCorreoIgnoreCase(correo)) {
      throw new ReglaNegocioException("Ya existe un usuario con el correo: " + correo);
    }
    if (usuarios.existsByRun(run)) {
      throw new ReglaNegocioException("Ya existe un usuario con el RUN: " + run);
    }

    Usuario usuario = new Usuario();
    usuario.setRun(run);
    usuario.setNombre(datos.nombre().trim());
    usuario.setApellidos(datos.apellidos().trim());
    usuario.setCorreo(correo);
    usuario.setPassword(encoder.encode(datos.password()));
    usuario.setRegion(datos.region());
    usuario.setComuna(datos.comuna());
    usuario.setDireccion(datos.direccion().trim());
    usuario.setRol("CLIENTE");
    return respuesta(usuarios.save(usuario));
  }

  // Renovación: con un token todavía válido se entrega uno nuevo con más tiempo
  public LoginResponse refrescar(String encabezado) {
    if (encabezado == null || !encabezado.startsWith("Bearer ")) {
      throw new BadCredentialsException("Token no enviado");
    }
    Claims claims;
    try {
      claims = jwt.validar(encabezado.substring(7));
    } catch (JwtException | IllegalArgumentException e) {
      throw new BadCredentialsException("Token inválido o expirado");
    }
    Usuario usuario = usuarios.findByCorreoIgnoreCase(claims.getSubject())
        .orElseThrow(() -> new BadCredentialsException("Usuario no existe"));
    return respuesta(usuario);
  }

  private LoginResponse respuesta(Usuario u) {
    return new LoginResponse(jwt.generar(u), u.getCorreo(), u.nombreCompleto(), u.getRol());
  }
}
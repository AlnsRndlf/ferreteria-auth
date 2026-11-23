package cl.duocuc.ferreteria_auth.controller;

import cl.duocuc.ferreteria_auth.dto.LoginRequest;
import cl.duocuc.ferreteria_auth.dto.LoginResponse;
import cl.duocuc.ferreteria_auth.dto.RegistroRequest;
import cl.duocuc.ferreteria_auth.dto.UsuarioResponse;
import cl.duocuc.ferreteria_auth.security.UsuarioAutenticado;
import cl.duocuc.ferreteria_auth.service.AuthService;
import cl.duocuc.ferreteria_auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Login, registro, perfil y renovación del token JWT")
public class AuthController {

  private final AuthService service;
  private final UsuarioService usuarios;

  public AuthController(AuthService service, UsuarioService usuarios) {
    this.service = service;
    this.usuarios = usuarios;
  }

  @Operation(summary = "Iniciar sesión: entrega el token JWT")
  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest datos) {
    return service.login(datos);
  }

  @Operation(summary = "Registrar un cliente nuevo (queda con rol CLIENTE)")
  @PostMapping("/registro")
  public ResponseEntity<LoginResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(datos));
  }

  @Operation(summary = "Renovar el token (usa el botón Authorize con el token vigente)")
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/refresh")
  public LoginResponse refrescar(
      @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String encabezado) {
    return service.refrescar(encabezado);
  }

  @Operation(summary = "Datos del usuario que inició sesión (Mi perfil)")
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/perfil")
  public UsuarioResponse perfil(@Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
    return usuarios.perfil(usuario.correo());
  }
}

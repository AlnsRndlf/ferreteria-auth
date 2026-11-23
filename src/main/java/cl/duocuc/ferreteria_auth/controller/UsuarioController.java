package cl.duocuc.ferreteria_auth.controller;

import cl.duocuc.ferreteria_auth.dto.UsuarioRequest;
import cl.duocuc.ferreteria_auth.dto.UsuarioResponse;
import cl.duocuc.ferreteria_auth.security.UsuarioAutenticado;
import cl.duocuc.ferreteria_auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

  private final UsuarioService service;

  public UsuarioController(UsuarioService service) {
    this.service = service;
  }

  @Operation(summary = "Listar todos los usuarios")
  @GetMapping
  public List<UsuarioResponse> listar() {
    return service.listar();
  }

  @Operation(summary = "Buscar un usuario por id")
  @GetMapping("/{id}")
  public UsuarioResponse buscar(@PathVariable Long id) {
    return service.buscar(id);
  }

  @Operation(summary = "Crear un usuario eligiendo su rol")
  @PostMapping
  public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest datos) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(datos));
  }

  @Operation(summary = "Actualizar un usuario (contraseña vacía = no cambiarla)")
  @PutMapping("/{id}")
  public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest datos) {
    return service.actualizar(id, datos);
  }

  @Operation(summary = "Eliminar un usuario (no se puede eliminar la propia cuenta)")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id,
      @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
    service.eliminar(id, usuario.correo());
  }
}

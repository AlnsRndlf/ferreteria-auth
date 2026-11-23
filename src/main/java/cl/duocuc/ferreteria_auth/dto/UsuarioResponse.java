package cl.duocuc.ferreteria_auth.dto;

// Nunca incluye la contraseña
public record UsuarioResponse(
    Long id,
    String run,
    String nombre,
    String apellidos,
    String correo,
    String region,
    String comuna,
    String direccion,
    String rol
) {}

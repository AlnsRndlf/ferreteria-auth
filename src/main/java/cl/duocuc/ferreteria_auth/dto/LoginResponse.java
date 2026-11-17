package cl.duocuc.ferreteria_auth.dto;

public record LoginResponse(
    String token,
    String correo,
    String nombre,
    String rol
) {}

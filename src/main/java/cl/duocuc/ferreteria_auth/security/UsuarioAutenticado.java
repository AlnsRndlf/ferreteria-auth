package cl.duocuc.ferreteria_auth.security;

// Los datos de la persona que mostró una pulsera (token) válida
public record UsuarioAutenticado(String correo, String nombre, String rol) {}

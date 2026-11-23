package cl.duocuc.ferreteria_auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Lo usa el ADMIN para crear o editar usuarios (puede elegir el rol)
public record UsuarioRequest(
    @NotBlank @Pattern(regexp = "[0-9]{6,8}[0-9Kk]", message = "RUN sin puntos ni guion, ej: 123456785") String run,
    @NotBlank @Size(max = 50) String nombre,
    @NotBlank @Size(max = 100) String apellidos,
    @NotBlank @Size(max = 100)
    @Pattern(regexp = "(?i)^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$",
        message = "solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com") String correo,
    @Size(min = 4, max = 10) String password, // obligatoria al crear; al editar, vacía = no cambiar
    @NotBlank @Size(max = 100) String region,
    @NotBlank @Size(max = 100) String comuna,
    @NotBlank @Size(max = 300) String direccion,
    @NotBlank @Pattern(regexp = "ADMIN|VENDEDOR|CLIENTE", message = "debe ser ADMIN, VENDEDOR o CLIENTE") String rol
) {}

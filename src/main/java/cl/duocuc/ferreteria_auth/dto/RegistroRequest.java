package cl.duocuc.ferreteria_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Email String correo,
    @NotBlank @Size(min = 4, max = 10) String password
) {}

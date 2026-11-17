package cl.duocuc.ferreteria_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank @Email String correo,
    @NotBlank String password
) {}

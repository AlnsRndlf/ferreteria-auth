package cl.duocuc.ferreteria_auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 10)
  private String run; // sin puntos ni guion, ej: 123456785

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(length = 100)
  private String apellidos;

  @Column(nullable = false, unique = true)
  private String correo;

  // Se guarda encriptada (BCrypt), nunca en texto plano
  @Column(nullable = false)
  private String password;

  @Column(length = 100)
  private String region;

  @Column(length = 100)
  private String comuna;

  @Column(length = 300)
  private String direccion;

  // ADMIN, VENDEDOR o CLIENTE
  @Column(nullable = false, length = 20)
  private String rol;

  public String nombreCompleto() {
    return apellidos == null || apellidos.isBlank() ? nombre : nombre + " " + apellidos;
  }
}

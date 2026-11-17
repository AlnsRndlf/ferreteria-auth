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

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, unique = true)
  private String correo;

  // Se guarda encriptada (BCrypt), nunca en texto plano
  @Column(nullable = false)
  private String password;

  // ADMIN, VENDEDOR o CLIENTE
  @Column(nullable = false, length = 20)
  private String rol;
}

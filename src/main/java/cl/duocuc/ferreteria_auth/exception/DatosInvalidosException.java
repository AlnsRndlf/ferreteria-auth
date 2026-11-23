package cl.duocuc.ferreteria_auth.exception;

// Datos que no cumplen una regla de validación (responde 400)
public class DatosInvalidosException extends RuntimeException {
  public DatosInvalidosException(String mensaje) {
    super(mensaje);
  }
}

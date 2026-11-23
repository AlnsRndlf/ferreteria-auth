package cl.duocuc.ferreteria_auth.validation;

// Valida el RUN chileno con el dígito verificador (módulo 11)
public final class ValidadorRun {

  private ValidadorRun() {}

  public static boolean esValido(String valor) {
    if (valor == null) {
      return false;
    }
    String run = valor.trim().toUpperCase();
    if (!run.matches("[0-9]{6,8}[0-9K]")) {
      return false;
    }

    String cuerpo = run.substring(0, run.length() - 1);
    char digitoVerificador = run.charAt(run.length() - 1);

    int suma = 0;
    int multiplicador = 2;
    for (int i = cuerpo.length() - 1; i >= 0; i--) {
      suma += (cuerpo.charAt(i) - '0') * multiplicador;
      multiplicador++;
      if (multiplicador > 7) {
        multiplicador = 2;
      }
    }

    int resultado = 11 - (suma % 11);
    char calculado;
    if (resultado == 11) {
      calculado = '0';
    } else if (resultado == 10) {
      calculado = 'K';
    } else {
      calculado = (char) ('0' + resultado);
    }
    return digitoVerificador == calculado;
  }
}

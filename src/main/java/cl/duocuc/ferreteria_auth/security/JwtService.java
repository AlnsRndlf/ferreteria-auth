package cl.duocuc.ferreteria_auth.security;

import cl.duocuc.ferreteria_auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.issuer}")
  private String issuer;

  @Value("${app.jwt.expiration-ms}")
  private long expirationMs;

  private SecretKey clave() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  // Crea la "pulsera": quién eres (correo), tu rol y hasta cuándo sirve
  public String generar(Usuario usuario) {
    Date ahora = new Date();
    return Jwts.builder()
        .issuer(issuer)
        .subject(usuario.getCorreo())
        .claim("rol", usuario.getRol())
        .claim("nombre", usuario.nombreCompleto())
        .issuedAt(ahora)
        .expiration(new Date(ahora.getTime() + expirationMs))
        .signWith(clave())
        .compact();
  }

  // Revisa que la pulsera sea auténtica, de esta app y que no haya vencido
  public Claims validar(String token) {
    return Jwts.parser()
        .verifyWith(clave())
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}

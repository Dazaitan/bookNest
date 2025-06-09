package com.eCommers.bookNest.config.jwt;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtService {
    //clave secreta utilizada para firmar y verificar tokens JWT
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 86400000; // Tiempo de expiración 24 horas

    public String generarToken(String correo, String rol) {
        //Generar el token utilizando el correo y el rol del usuario
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims validarToken(String token) {
        //verificar que el token es válido y extraer datos.
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("🔍 Claims extraídos del token: " + claims);
            return claims;
        } catch (Exception e) {
            System.out.println("❌ Error al validar el token: " + e.getMessage());
            return null;
        }
    }
}

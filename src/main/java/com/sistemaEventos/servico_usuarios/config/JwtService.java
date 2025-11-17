package com.sistemaEventos.servico_usuarios.config;

import com.sistemaEventos.servico_usuarios.model.Role;
import com.sistemaEventos.servico_usuarios.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final long expirationTime;

    // 1. O Construtor lê a Chave Privada do application.yml
    public JwtService(@Value("${jwt.private-key}") String privateKey, @Value("${jwt.expiration-time}") int expirationTime) {
        try {
            String privateKeyString = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(privateKeyString);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.privateKey = kf.generatePrivate(spec);
            this.expirationTime = expirationTime;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar chave privada", e);
        }
    }

    // 2. O Método que GERA o token
    public String gerarToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getEmail()) // O "dono" do token
                .setIssuer("servico-usuarios") // Quem emitiu
                .setIssuedAt(now) // Data de emissão
                .setExpiration(expiration) // Data de expiração

                .claim("userId", user.getId()) //Envia o ID do usuário.
                .claim("userRoles", roleNames) //Envia as roles do usuário.

                //    Assina o token usando o algoritmo RS256 e a Chave Privada
                .signWith(this.privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}

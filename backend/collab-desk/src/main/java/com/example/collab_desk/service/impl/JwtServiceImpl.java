package com.example.collab_desk.service.impl;

import com.example.collab_desk.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Long expiry;

    public JwtServiceImpl(JwtEncoder jwtEncoder,
                          @Value("${jwt.issuer}") String issuer,
                          @Value("${jwt.expiry}") Long expiry
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expiry = expiry;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        List<String> authorities =
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return jwt.getTokenValue();
    }
}

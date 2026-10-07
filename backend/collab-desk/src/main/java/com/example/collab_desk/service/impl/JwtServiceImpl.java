package com.example.collab_desk.service.impl;

import com.example.collab_desk.entity.User;
import com.example.collab_desk.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final String issuer;
    private final Long expiry;

    public JwtServiceImpl(JwtEncoder jwtEncoder,
                          JwtDecoder jwtDecoder,
                          @Value("${jwt.issuer}") String issuer,
                          @Value("${jwt.expiry}") Long expiry
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
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

    @Override
    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant exp = jwt.getExpiresAt();

            boolean sameUser = user.getUsername().equals(jwt.getSubject());
            boolean notExpired = exp != null && exp.isAfter(Instant.now());

            return sameUser && notExpired;
        } catch (JwtException e) {
            return false;
        }
    }


}

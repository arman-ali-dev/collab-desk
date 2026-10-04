package com.example.collab_desk.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];   // 256-bit token
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

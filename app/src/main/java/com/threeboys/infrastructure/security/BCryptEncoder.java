package com.threeboys.infrastructure.security;

import com.threeboys.application.session.Encoder;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptEncoder implements Encoder {

    @Override
    public String encode(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    @Override
    public boolean matches(String raw, String stored) {
        if (raw == null || stored == null || stored.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(raw, stored);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
}
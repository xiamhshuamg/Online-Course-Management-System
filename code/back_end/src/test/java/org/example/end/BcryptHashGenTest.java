package org.example.end;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility test to generate a BCrypt hash for seed SQL.
 * Run:
 *   ./mvnw -q -Dtest=BcryptHashGenTest test
 */
public class BcryptHashGenTest {

    @Test
    void printHash() {
        System.out.println(new BCryptPasswordEncoder().encode("admin1234"));
    }
}



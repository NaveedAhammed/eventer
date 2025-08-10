package com.eventer.userservice.repository;

import com.eventer.userservice.entity.User;
import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@gmail.com")
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .firstName("test")
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        user = null;
        userRepository.deleteAll();
    }

    @Test
    void testFindByEmail_Found(){
        Optional<User> foundUser = userRepository.findByEmail("test@gmail.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@gmail.com");
        assertThat(foundUser.get().getFirstName()).isEqualTo("test");
        assertThat(foundUser.get().getRole()).isEqualTo(Role.USER);
        assertThat(foundUser.get().getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
    }

    @Test
    void testFindByEmail_NotFound(){
        Optional<User> foundUser = userRepository.findByEmail("dummy@gmail.com");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void testExistsByEmail_Found(){
        boolean existingUser = userRepository.existsByEmail("test@gmail.com");
        assertThat(existingUser).isTrue();
    }

    @Test
    void testExistsByEmail_NotFound(){
        boolean existingUser = userRepository.existsByEmail("dummy@gmail.com");
        assertThat(existingUser).isFalse();
    }

    @TestConfiguration
    static class TestAuditConfig {
        @Bean
        public AuditorAware<String> auditAwareImpl() {
            return () -> Optional.of("USERS_MS");
        }
    }
}

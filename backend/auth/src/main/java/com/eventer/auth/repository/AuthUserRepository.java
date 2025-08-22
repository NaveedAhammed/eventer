package com.eventer.auth.repository;

import com.eventer.auth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {
    boolean existsByEmail(String email);

    Optional<AuthUser> findByEmail(String email);

    @Query("SELECT authUser FROM AuthUser authUser WHERE authUser.username = :usernameOrEmail OR authUser.email = :usernameOrEmail")
    Optional<AuthUser> findByUsernameOrEmail(String usernameOrEmail);
}

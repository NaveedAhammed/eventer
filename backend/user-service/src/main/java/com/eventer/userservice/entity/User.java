package com.eventer.userservice.entity;

import com.eventer.userservice.entity.enums.AuthProvider;
import com.eventer.userservice.entity.enums.BaseEntity;
import com.eventer.userservice.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID userId;

    @NonNull
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String profilePictureUrl;

    private String location;

    private Double latitude;

    private Double longitude;

    private String phone;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean verified = false;
}

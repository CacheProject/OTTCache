package com.example.cacheproject.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.cacheproject.domain.user.enums.UserRole;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public User(String email, String username, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = UserRole.of(role);
    }

    public void updateUsername(String newName) {
        this.username = newName;
    }
}

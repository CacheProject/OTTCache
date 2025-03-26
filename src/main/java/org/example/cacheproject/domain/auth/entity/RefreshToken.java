package org.example.cacheproject.domain.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.cacheproject.domain.auth.enums.TokenStatus;

import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(STRING)
    private TokenStatus tokenStatus = TokenStatus.VALID;

    public RefreshToken(Long userId) {
        this.userId = userId;
        this.token = UUID.randomUUID().toString();
        this.tokenStatus = TokenStatus.VALID;
    }

    public void updateTokenStatus(TokenStatus status) {
        this.tokenStatus = status;
    }


}

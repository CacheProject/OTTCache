package org.example.cacheproject.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordCheck;

    @NotBlank
    private String role;

    private StoreRequestDto store;
}

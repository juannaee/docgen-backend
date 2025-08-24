package com.example.docgen.dto.user;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class PasswordResetRequestDTO {

    private String newPassword;
    private String confirmPassword;


}

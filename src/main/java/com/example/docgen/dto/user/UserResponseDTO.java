package com.example.docgen.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Boolean passwordResetRequired;
}




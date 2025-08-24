package com.example.docgen.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserUpdateDTO {

    @Size(min = 8, max = 50, message = "Seu nome deve ter entre 3 e 50 caracteres")
    private String name;
    @Size(min = 8, max = 20, message = "Telefone deve ter entre 8 e 20 caracteres")
    private String phone;


}

package com.example.docgen.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class FailedUserDTO {

    private String email;
    private String reason;


}

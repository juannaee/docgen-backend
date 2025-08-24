package com.example.docgen.dto.user;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class FailedUserDTO {

    private String email;
    private String reason;


}

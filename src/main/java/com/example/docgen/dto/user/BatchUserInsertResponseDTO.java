package com.example.docgen.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@Data
public class BatchUserInsertResponseDTO {

    private List<UserResponseDTO> successUsers;
    private List<FailedUserDTO> failedUsers;


}

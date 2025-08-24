package com.example.docgen.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BatchUserInsertResponseDTO {

    private List<UserResponseDTO> successUsers;
    private List<FailedUserDTO> failedUsers;


}

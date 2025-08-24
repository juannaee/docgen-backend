package com.example.docgen.mappers;

import com.example.docgen.dto.user.UserRequestDTO;
import com.example.docgen.dto.user.UserResponseDTO;
import com.example.docgen.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-24T10:39:02-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setRole( com.example.docgen.entities.enums.UserRole.USER );

        formatCpfAfterToEntity( user );

        return user;
    }

    @Override
    public UserResponseDTO toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        return userResponseDTO;
    }

    @Override
    public List<UserResponseDTO> toDtoList(List<User> user) {
        if ( user == null ) {
            return null;
        }

        List<UserResponseDTO> list = new ArrayList<UserResponseDTO>( user.size() );
        for ( User user1 : user ) {
            list.add( toDto( user1 ) );
        }

        return list;
    }
}

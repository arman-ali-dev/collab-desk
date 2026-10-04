package com.example.collab_desk.dto.responseDto;

import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileResponseDto {
    private Long  id;
    private String fullName;
    private String email;
    private String designation;
    private UserRole role;
    private String profileImage;
    private UserStatus status;
}

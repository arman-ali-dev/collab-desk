package com.example.collab_desk.dto.requestDto;

import com.example.collab_desk.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMemberRequestDto {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "Designation cannot be blank")
    @Size(max = 150, message = "Full name cannot exceed 150 characters")
    private String designation;

    @NotNull(message = "Role is required")
    private UserRole role;
}

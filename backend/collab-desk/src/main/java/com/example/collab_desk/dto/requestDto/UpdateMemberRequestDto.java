package com.example.collab_desk.dto.requestDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateMemberRequestDto {
    @NotEmpty(message = "At least one member is required")
    @Size(max = 50, message = "Cannot assign more than 50 members")
    private List<@NotNull(message = "User id cannot be null")
    @Positive(message = "User must be greater than 0") Long> assignedTo;
}

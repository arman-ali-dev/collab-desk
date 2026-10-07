package com.example.collab_desk.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SenderResponseDto {
    private Long id;
    private String fullName;
    private String profileImage;
}

package com.auth.dto.response;

public record UserProfileResponseDTO(

        Long id,

        String name,

        String email,

        String mobile,

        String role,

        String status
) {
}

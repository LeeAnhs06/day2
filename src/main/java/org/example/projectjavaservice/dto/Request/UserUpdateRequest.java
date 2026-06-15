package org.example.projectjavaservice.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email invalid format")
    private String email;

    @Pattern(regexp = "^\\d{9,15}$", message = "Phone number must contain 9 to 15 digits")
    private String phoneNumber;
}
package com.support.desk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 8, max = 16)
    private String password;

    @Min(value = 16, message = "Age must be at least 16")
    private int age;

    @NotBlank
    private String gender;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @Size(max = 10)
    private String phoneNumber;

    // For employees
    private String employeeCode;
    private String department;
}
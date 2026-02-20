package com.shaadiseva.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpVerifyRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be a 10-digit number")
    private String phone;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be a 6-digit number")
    private String otp;
}

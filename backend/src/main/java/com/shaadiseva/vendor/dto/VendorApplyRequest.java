package com.shaadiseva.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class VendorApplyRequest {

    private String email;

    private String phone;

    private String password;

    @NotBlank(message = "Business name is required")
    private String businessName;

    private UUID categoryId;

    private String gstNumber;

    private String panNumber;

    private String address;

    private String city;

    private String bio;

    private Integer yearsExperience;
}

package com.customer.dto.response;

import com.customer.enums.CustomerStatus;
import com.customer.enums.KYCStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String ssn;
    private CustomerStatus status;
    private KYCStatus kycStatus;
    private String occupation;
    private String employer;
    private Double annualIncome;
    private List<AddressResponse> addresses;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
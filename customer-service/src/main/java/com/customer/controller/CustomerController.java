package com.customer.controller;


import com.customer.dto.request.CustomerRequest;
import com.customer.dto.response.CustomerResponse;
import com.customer.enums.CustomerStatus;
import com.customer.enums.KYCStatus;
import com.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerService;

    @PostMapping("/create-customer")
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request,
            @RequestHeader("email") String email) {
        // Only admins or users with proper permissions can create customers
        CustomerResponse response = customerService.createCustomer(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customer-details/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable Long id,
            @RequestHeader("userId") String userId,
            @RequestHeader("roles") String roles,
            @RequestHeader("email") String username) throws AccessDeniedException {
        
        // Fine-grained authorization
        if (!canAccessCustomer(userId, roles, id)) {
            log.warn("User {} attempted to access customer {} without permission", username, id);
            throw new AccessDeniedException("You don't have permission to access this customer");
        }
        CustomerResponse response = customerService.getCustomer(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("roles") String roles) throws AccessDeniedException {
        
        // Only admins can list all customers
        if (!isAdmin(roles)) {
            throw new AccessDeniedException("Only administrators can list all customers");
        }
        
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(
            @RequestParam String term,
            @RequestHeader("roles") String roles) throws AccessDeniedException {
        
        if (!isAdmin(roles)) {
            throw new AccessDeniedException("Only administrators can search customers");
        }
        
        return ResponseEntity.ok(customerService.searchCustomers(term));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request,
            @RequestHeader("userId") String userId,
            @RequestHeader("roles") String roles,
            @RequestHeader("email") String username) throws AccessDeniedException {
        
        // Only admins or the customer themselves can update
        if (!canUpdateCustomer(userId, roles, id)) {
            log.warn("User {} attempted to update customer {} without permission", username, id);
            throw new AccessDeniedException("You don't have permission to update this customer");
        }
        
        CustomerResponse response = customerService.updateCustomer(id, request, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteCustomer(
             @RequestParam("ids")  List<String> ids,
            @RequestHeader("roles") String roles,
            @RequestHeader("email") String username) throws AccessDeniedException {
        
        // Only admins can delete customers
        if (!isAdmin(roles)) {
            throw new AccessDeniedException("Only administrators can delete customers");
        }
        
        customerService.deleteCustomer(ids, username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/kyc")
    public ResponseEntity<CustomerResponse> updateKYCStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("roles") String roles,
            @RequestHeader("email") String username) throws AccessDeniedException {
        
        // Only admins can update KYC status
        if (!isAdmin(roles)) {
            throw new AccessDeniedException("Only administrators can update KYC status");
        }
        
        CustomerResponse response = customerService.updateKYCStatus(id, status, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<CustomerResponse> activateCustomer(
            @PathVariable Long id,
            @RequestHeader("roles") String roles,
            @RequestHeader("email") String username) throws AccessDeniedException {
        
        if (!isAdmin(roles)) {
            throw new AccessDeniedException("Only administrators can activate customers");
        }
        
        CustomerResponse response = customerService.activateCustomer(id, username);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<CustomerResponse> suspendCustomer(
            @PathVariable Long id,
            @RequestHeader("roles") String roles,
            @RequestHeader("email") String username) throws AccessDeniedException {
        
        if (!isAdmin(roles)) {
            throw new AccessDeniedException("Only administrators can suspend customers");
        }
        
        CustomerResponse response = customerService.suspendCustomer(id, username);
        return ResponseEntity.ok(response);
    }

    // Private authorization methods
    private boolean canAccessCustomer(String userId, String roles, Long customerId) {
        if (isAdmin(roles)) {
            return true;
        }
        // Users can only access their own customer data
        // Assumes userId is the same as customerId (or we have mapping)
        return userId.equals(String.valueOf(customerId));
    }

    private boolean canUpdateCustomer(String userId, String roles, Long customerId) {
        if (isAdmin(roles)) {
            return true;
        }
        // Users can only update their own customer data
        return userId.equals(String.valueOf(customerId));
    }

    private boolean isAdmin(String roles) {
        return roles != null && roles.contains("ADMIN");
    }

    @GetMapping("/{customerNumber}")
    public ResponseEntity<CustomerResponse> getCustomerByCustomerNumber(@PathVariable String customerNumber) {
        return ResponseEntity.ok( customerService.findByCustomerNumber(customerNumber));
    }

  @GetMapping("/customer-id")
  public ResponseEntity<String> getCustomerByEmail(@RequestParam("email") String email) {
    return ResponseEntity.ok( customerService.getCustomerByEmail(email));
  }

    /**
     * Get customers by status
     * @param status the customer status
     * @return List of customers with given status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByStatus(@PathVariable CustomerStatus status) {
        List<CustomerResponse> customers = customerService.findByStatus(status);

        return ResponseEntity.ok(customers);
    }

    /**
     * Get customers by KYC status
     * @param status the KYC status
     * @return List of customers with given KYC status
     */
    @GetMapping("/kyc/{status}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByKycStatus(@PathVariable KYCStatus status) {
        List<CustomerResponse> customers = customerService.findByKycStatus(status);
        return ResponseEntity.ok(customers);
    }

    /**
     * Get active customers with specific KYC status
     * @param status the KYC status
     * @return List of active customers with given KYC status
     */
    @GetMapping("/active/kyc/{status}")
    public ResponseEntity<List<CustomerResponse>> getActiveCustomersWithKYCStatus(@PathVariable KYCStatus status) {
        List<CustomerResponse> customers = customerService.findActiveCustomersWithKYCStatus(status);
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customers);
    }

    /**
     * Count customers created between two dates
     * @param startDate start date (format: yyyy-MM-ddTHH:mm:ss)
     * @param endDate end date (format: yyyy-MM-ddTHH:mm:ss)
     * @return Count of customers created in the date range
     */
    @GetMapping("/count/created-between")
    public ResponseEntity<Long> countCustomersCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(customerService.countCustomersCreatedBetween(startDate, endDate));
    }
}
package com.customer.service;

import com.customer.dto.request.AddressRequest;
import com.customer.dto.request.CustomerRequest;
import com.customer.dto.response.AddressResponse;
import com.customer.dto.response.CustomerResponse;
import com.customer.entity.Address;
import com.customer.entity.Customer;
import com.customer.enums.CustomerStatus;
import com.customer.enums.KYCStatus;
import com.customer.exception.BusinessException;
import com.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, String email) {
        log.info("CustomerService: Request received from api-gateway");
        if(customerRepository.existsByEmail(email)){
        throw new BusinessException("Customer is already exist.");
        }
        String customerNumber = getCustomerNumber();
        Customer customer = Customer.builder()
                .customerNumber(customerNumber).firstName(request.getFirstName()).lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth()).phoneNumber(request.getPhoneNumber()).email(email).
                status(CustomerStatus.PENDING).kycStatus(KYCStatus.NOT_SUBMITTED)
                .annualIncome(request.getAnnualIncome()).employer(request.getEmployer()).ssn(request.getSsn())
                        .occupation(request.getOccupation()).middleName(request.getMiddleName()).active(true).build();
        if(!CollectionUtils.isEmpty(request.getAddresses())){
            List<Address> addresses = request.getAddresses().stream().map(this::getAddress).toList();
            customer.setAddresses(addresses);
        }
        customerRepository.save(customer);
        return convertToResponse(customer);
    }

    private CustomerResponse convertToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerNumber(customer.getCustomerNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .middleName(customer.getMiddleName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .dateOfBirth(customer.getDateOfBirth())
                .ssn(customer.getSsn())
                .status(customer.getStatus())
                .kycStatus(customer.getKycStatus())
                .occupation(customer.getOccupation())
                .employer(customer.getEmployer())
                .annualIncome(customer.getAnnualIncome())
                .addresses(getAddressResponse(customer.getAddresses()))
                .active(customer.isActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    private List<AddressResponse> getAddressResponse(List<Address> addresses) {
        return addresses.stream().map(address -> AddressResponse.builder()
                .addressLine1(address.getAddressLine1()).addressLine2(address.getAddressLine2())
                .city(address.getCity()).id(address.getId()).primaryAddress(address.isPrimaryAddress()).
        type(address.getType()).state(address.getState()).
                country(address.getCountry()).postalCode(address.getPostalCode())
                .build()).toList();
    }

    private Address getAddress(AddressRequest addressRequest) {
        return Address.builder().addressLine1(addressRequest.getAddressLine1()).addressLine2(addressRequest.getAddressLine2())
                .city(addressRequest.getCity()).primaryAddress(addressRequest.isPrimaryAddress()).state(addressRequest.getState())
                .postalCode(addressRequest.getPostalCode()).type(addressRequest.getType()).country(addressRequest.getCountry())
                .build();
    }

    private String getCustomerNumber() {
        String prefix = "CUS";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(4);
        String uuid = UUID.randomUUID().toString().substring(0,8);
        return prefix + timestamp + uuid;
    }

    @Cacheable(value = "customer",key = "#id")
    public CustomerResponse getCustomer(Long id) {
        Customer customer = findCustomerById(id);
        return convertToResponse(customer);
    }

    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    public List<CustomerResponse> searchCustomers(String searchTerm) {
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        searchTerm, searchTerm).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    @CachePut(value = "customer",key = "#id")
    public CustomerResponse updateCustomer(Long id, CustomerRequest request, String updatedBy) {
        Customer customer = findCustomerById(id);

        // Update fields
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setMiddleName(request.getMiddleName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setOccupation(request.getOccupation());
        customer.setEmployer(request.getEmployer());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setUpdatedBy(updatedBy);

        // Update addresses
        if (request.getAddresses() != null) {
            customer.getAddresses().clear();
            List<Address> addresses = request.getAddresses().stream()
                    .map(adderRequest -> Address.builder()
                            .addressLine1(adderRequest.getAddressLine1())
                            .addressLine2(adderRequest.getAddressLine2())
                            .city(adderRequest.getCity())
                            .state(adderRequest.getState())
                            .postalCode(adderRequest.getPostalCode())
                            .country(adderRequest.getCountry())
                            .primaryAddress(adderRequest.isPrimaryAddress())
                            .type(adderRequest.getType())
                            .customer(customer)
                            .build())
                    .collect(Collectors.toList());
            customer.getAddresses().addAll(addresses);
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated: {} with ID: {}", updatedCustomer.getEmail(), updatedCustomer.getId());

        return convertToResponse(updatedCustomer);
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new BusinessException("Customer Not Found."));
    }

    @Transactional
    @CacheEvict(cacheNames = "customer",key = "#id")
    public void deleteCustomer(Long id, String updatedBy) {
        Customer customer =findCustomerById(id);
        customer.setUpdatedBy(updatedBy);
        customer.setActive(false);
        customer.setStatus(CustomerStatus.CLOSED);
        // dirty checking is here

    }
    @Transactional
    @CachePut(value = "customer",key = "#id")
    public CustomerResponse updateKYCStatus(Long id, String status, String updatedBy) {
        Customer customer = findCustomerById(id);
        KYCStatus kycStatus = KYCStatus.valueOf(status);
        customer.setKycStatus(kycStatus);
        customer.setUpdatedBy(updatedBy);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("KYC status updated for customer {} to: {}", customer.getEmail(), status);
        return convertToResponse(updatedCustomer);
    }
    @Transactional
    @CachePut(value = "customer",key = "#id")
    public CustomerResponse activateCustomer(Long id, String activatedBy) {
        Customer customer = findCustomerById(id);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setUpdatedBy(activatedBy);
        log.info("Customer activated: {} by: {}", customer.getEmail(), activatedBy);
        return convertToResponse(customer);
    }
    @Transactional
    @CachePut(value = "customer",key = "#id")
    public CustomerResponse suspendCustomer(Long id, String suspendedBy) {
        Customer customer = findCustomerById(id);
        customer.setStatus(CustomerStatus.SUSPENDED);
        customer.setUpdatedBy(suspendedBy);
        log.info("Customer suspended: {} by: {}", customer.getEmail(), suspendedBy);
        return convertToResponse(customer);
    }

    @Cacheable(cacheNames = "customer",key = "#customerNumber")
    public CustomerResponse findByCustomerNumber(String customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new BusinessException("Customer Not Found By Customer Number"));
        return convertToResponse(customer);

    }

    public List<CustomerResponse> findByStatus(CustomerStatus status) {
        List<Customer> customers = customerRepository.findByStatus(status);
        if(CollectionUtils.isEmpty(customers)){
            return Collections.emptyList();
        }
        return customers.stream().map(this::convertToResponse).toList();
    }

    public List<CustomerResponse> findByKycStatus(KYCStatus status) {
        List<Customer> customers = customerRepository.findByKycStatus(status);
        if(CollectionUtils.isEmpty(customers)){
            return Collections.emptyList();
        }
        return customers.stream().map(this::convertToResponse).toList();
    }


    public List<CustomerResponse> findActiveCustomersWithKYCStatus(KYCStatus status) {
        List<Customer> customers = customerRepository.findActiveCustomersWithKYCStatus(status);
        if(CollectionUtils.isEmpty(customers)){
            return Collections.emptyList();
        }
        return customers.stream().map(this::convertToResponse).toList();
    }

    public Long countCustomersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return customerRepository.countCustomersCreatedBetween(startDate,endDate);
    }
}

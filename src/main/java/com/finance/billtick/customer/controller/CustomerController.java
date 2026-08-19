package com.finance.billtick.customer.controller;

import com.finance.billtick.customer.dto.CustomerPatchRequest;
import com.finance.billtick.customer.dto.CustomerRequest;
import com.finance.billtick.customer.dto.CustomerResponse;
import com.finance.billtick.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping()
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequest));
    }

    @GetMapping()
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerRequest));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<CustomerResponse> patchCustomer(@PathVariable Long id, @Valid @RequestBody CustomerPatchRequest customerPatchRequest) {
        return ResponseEntity.ok(customerService.patchCustomer(id, customerPatchRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/business")
    public ResponseEntity<List<CustomerResponse>> getAllCustomersForBusiness(@RequestParam Long businessId) {
        return ResponseEntity.ok(customerService.getAllCustomersForBusiness(businessId));
    }



}

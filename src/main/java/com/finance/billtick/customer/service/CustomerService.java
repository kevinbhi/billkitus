package com.finance.billtick.customer.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.customer.dto.CustomerPatchRequest;
import com.finance.billtick.customer.dto.CustomerRequest;
import com.finance.billtick.customer.dto.CustomerResponse;
import com.finance.billtick.customer.mapper.CustomerMapper;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.customer.repository.CustomerRepository;
import com.finance.billtick.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BusinessRepository businessRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        Customer customer = customerMapper.toCustomer(customerRequest);
        customer.setBusiness(assertBusiness(customerRequest.getBusinessId()));
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerMapper.toCustomerResponseList(customerRepository.findAll());
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {
        Customer customer = assertCustomer(id);
        customerMapper.updateCustomer(customerRequest, customer);
        customer.setBusiness(assertBusiness(customerRequest.getBusinessId()));
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse patchCustomer(Long id, CustomerPatchRequest customerPatchRequest) {
        Customer customer = assertCustomer(id);
        customerMapper.patchCustomer(customerPatchRequest, customer);
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = assertCustomer(id);
        customerRepository.delete(customer);
    }

    private Customer assertCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    public List<CustomerResponse> getAllCustomersForBusiness(Long businessId) {
        return customerMapper.toCustomerResponseList(customerRepository.findByBusiness(assertBusiness(businessId)));
    }
}

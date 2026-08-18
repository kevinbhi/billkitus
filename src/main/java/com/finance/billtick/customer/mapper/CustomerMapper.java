package com.finance.billtick.customer.mapper;

import com.finance.billtick.customer.dto.CustomerPatchRequest;
import com.finance.billtick.customer.dto.CustomerRequest;
import com.finance.billtick.customer.dto.CustomerResponse;
import com.finance.billtick.customer.model.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(target = "business", ignore = true)
    Customer toCustomer(CustomerRequest customerRequest);

    @Mapping(source = "business.id", target = "businessId")
    CustomerResponse toCustomerResponse(Customer customer);

    List<CustomerResponse> toCustomerResponseList(List<Customer> customers);

    @Mapping(target = "business", ignore = true)
    void updateCustomer(CustomerRequest customerRequest, @MappingTarget Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "business", ignore = true)
    void patchCustomer(CustomerPatchRequest customerRequest, @MappingTarget Customer customer);
}

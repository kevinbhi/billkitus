package com.finance.billtick.customer.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByBusiness(Business business);
}

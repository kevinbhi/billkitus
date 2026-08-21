package com.finance.billtick.invoice.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByBusiness(Business business);

    List<Invoice> findByCustomer(Customer customer);

    boolean existsByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumberAndIdNot(String invoiceNumber, Long id);

    // Latest invoice for a business whose number starts with "PREFIX-YEAR-".
    // Works because the sequence is fixed-width zero-padded, so lexical desc == numeric desc.
    Optional<Invoice> findFirstByBusinessAndInvoiceNumberStartingWithOrderByInvoiceNumberDesc(
            Business business, String invoiceNumberPrefix);
}

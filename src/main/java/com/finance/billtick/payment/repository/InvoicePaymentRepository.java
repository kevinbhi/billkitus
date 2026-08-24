package com.finance.billtick.payment.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.payment.model.InvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, Long> {
    // Payment history for one invoice, newest first. Also the source of the balance recompute,
    // so the @SQLRestriction on InvoicePayment excludes reversed rows from both.
    List<InvoicePayment> findByInvoiceOrderByPaymentDateDescIdDesc(Invoice invoice);

    List<InvoicePayment> findByBusiness(Business business);

    List<InvoicePayment> findByCustomer(Customer customer);

    boolean existsByInvoice(Invoice invoice);
}

package com.finance.billtick.payment.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.payment.model.InvoicePayment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, Long> {


    @Override
    @EntityGraph(attributePaths = "invoice")
    List<InvoicePayment> findAll();

    @Override
    @EntityGraph(attributePaths = "invoice")
    Optional<InvoicePayment> findById(Long id);


    List<InvoicePayment> findByInvoiceOrderByPaymentDateDescIdDesc(Invoice invoice);

    @Query("select sum(p.amount) from InvoicePayment p where p.invoice = :invoice")
    BigDecimal sumAmountByInvoice(@Param("invoice") Invoice invoice);

    @EntityGraph(attributePaths = "invoice")
    List<InvoicePayment> findByBusiness(Business business);

    @EntityGraph(attributePaths = "invoice")
    List<InvoicePayment> findByCustomer(Customer customer);

    boolean existsByInvoice(Invoice invoice);
}

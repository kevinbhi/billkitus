package com.finance.billtick.invoice.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.model.PaymentStatus;
import com.finance.billtick.product.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // InvoiceResponse carries the line items, so every list mapping initialises the lazy
    // items collection -- one SELECT per invoice without these. Hibernate 6+ de-duplicates
    // collection fetches automatically, so no distinct is needed (and adding one would be wrong).
    // NOTE for whoever adds pagination: @EntityGraph on a collection + Pageable makes Hibernate
    // fetch everything and paginate in memory (HHH000104). Switch to a two-step id query then.
    @Override
    @EntityGraph(attributePaths = "items")
    List<Invoice> findAll();

    @EntityGraph(attributePaths = "items")
    List<Invoice> findByBusiness(Business business);

    // Highest invoice number for a business matching "PREFIX-YEAR-%". Fixed-width zero padding
    // makes lexical max == numeric max.
    //
    // Deliberately native: @SQLRestriction is applied to every entity root regardless of the
    // select clause, so no JPQL or derived query can see soft-deleted invoices -- and an ISSUED
    // number must never be reused, even if its invoice was later deleted. Reusing one is an
    // accounting error, and it also collides with the (unfiltered) uk_invoice_business index.
    // Returns null when the business has no invoices for that year.
    @Query(value = """
           select max(invoice_number) from invoice
           where business_id = :businessId and invoice_number like :pattern escape '\\'
           """, nativeQuery = true)
    String findMaxInvoiceNumber(@Param("businessId") Long businessId, @Param("pattern") String pattern);

    @EntityGraph(attributePaths = "items")
    List<Invoice> findByCustomer(Customer customer);

    boolean existsByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumberAndIdNot(String invoiceNumber, Long id);

    // Issued, not fully settled, and past due. Overdue is never stored, so it is expressed here.
    @EntityGraph(attributePaths = "items")
    List<Invoice> findByBusinessAndStatusAndPaymentStatusNotAndDueDateBefore(
            Business business, InvoiceStatus status, PaymentStatus paymentStatus, LocalDate date);

    // Every dashboard scalar in one pass, so the service never materialises an Invoice.
    // count(case .. then 1 end) rather than sum(case .. then 1 else 0 end): count over an
    // all-null set returns 0 rather than null, which keeps the primitive long components safe.
    // The sums carry no else arm on purpose -- it avoids unifying BigDecimal with an integer
    // literal -- so they are null only when the business has zero invoices; scale() absorbs it.
    // The unpaid bucket counts OVERDUE as well -- that is an unpaid invoice past its due date,
    // so leaving it out would stop the three status counts summing to the invoice count.
    @Query("""
           select new com.finance.billtick.invoice.repository.InvoiceTotals(
             sum(case when i.status = :sent then i.total end),
             sum(case when i.status <> :voided then i.total - i.balanceDue end),
             sum(case when i.status = :sent then i.balanceDue end),
             sum(case when i.status = :sent and i.paymentStatus <> :paid
                       and i.dueDate < :asOf and i.balanceDue > 0 then i.balanceDue end),
             count(case when i.status = :sent and i.paymentStatus <> :paid
                         and i.dueDate < :asOf and i.balanceDue > 0 then 1 end),
             count(case when i.status = :draft then 1 end),
             count(case when i.status = :sent then 1 end),
             count(case when i.status = :voided then 1 end),
             count(case when i.paymentStatus in (:unpaid, :overdue) then 1 end),
             count(case when i.paymentStatus = :partiallyPaid then 1 end),
             count(case when i.paymentStatus = :paid then 1 end))
           from Invoice i
           where i.business = :business
           """)
    InvoiceTotals aggregateTotals(@Param("business") Business business,
                                  @Param("asOf") LocalDate asOf,
                                  @Param("draft") InvoiceStatus draft,
                                  @Param("sent") InvoiceStatus sent,
                                  @Param("voided") InvoiceStatus voided,
                                  @Param("unpaid") PaymentStatus unpaid,
                                  @Param("partiallyPaid") PaymentStatus partiallyPaid,
                                  @Param("paid") PaymentStatus paid,
                                  @Param("overdue") PaymentStatus overdue);

    // AR aging, one sum + one count per bucket in a single row. The day-offset comparison
    // inverts into four cut-off dates computed in Java, so this needs no function('datediff',..)
    // and stays portable JPQL. See AgingTotals for why this is not a GROUP BY over a CASE chain.
    @Query("""
           select new com.finance.billtick.invoice.repository.AgingTotals(
             sum(case when i.dueDate >= :asOf then i.balanceDue end),
             count(case when i.dueDate >= :asOf then 1 end),
             sum(case when i.dueDate < :asOf and i.dueDate >= :day30 then i.balanceDue end),
             count(case when i.dueDate < :asOf and i.dueDate >= :day30 then 1 end),
             sum(case when i.dueDate < :day30 and i.dueDate >= :day60 then i.balanceDue end),
             count(case when i.dueDate < :day30 and i.dueDate >= :day60 then 1 end),
             sum(case when i.dueDate < :day60 and i.dueDate >= :day90 then i.balanceDue end),
             count(case when i.dueDate < :day60 and i.dueDate >= :day90 then 1 end),
             sum(case when i.dueDate < :day90 then i.balanceDue end),
             count(case when i.dueDate < :day90 then 1 end))
           from Invoice i
           where i.business = :business and i.status = :sent and i.balanceDue > 0
           """)
    AgingTotals aggregateAging(@Param("business") Business business,
                               @Param("sent") InvoiceStatus sent,
                               @Param("asOf") LocalDate asOf,
                               @Param("day30") LocalDate day30,
                               @Param("day60") LocalDate day60,
                               @Param("day90") LocalDate day90);

}

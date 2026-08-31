package com.finance.billtick.invoice.scheduler;

import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceStatus;
import com.finance.billtick.invoice.model.PaymentStatus;
import com.finance.billtick.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueInvoiceScheduler {

    private final InvoiceRepository invoiceRepository;


    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markOverdueInvoices() {
        LocalDate today = LocalDate.now();
        List<Invoice> invoices = invoiceRepository.findByStatusAndPaymentStatusInAndDueDateBefore(
                InvoiceStatus.SENT, List.of(PaymentStatus.UNPAID, PaymentStatus.PARTIAL), today);

        for (Invoice invoice : invoices) {
            invoice.setPaymentStatus(PaymentStatus.OVERDUE);
        }
        invoiceRepository.saveAll(invoices);

        log.info("Overdue invoice scheduler: marked {} invoice(s) OVERDUE as of {}", invoices.size(), today);
    }
}

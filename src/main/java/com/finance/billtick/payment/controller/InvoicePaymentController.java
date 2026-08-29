package com.finance.billtick.payment.controller;

import com.finance.billtick.payment.dto.InvoicePaymentRequest;
import com.finance.billtick.payment.dto.InvoicePaymentResponse;
import com.finance.billtick.payment.service.InvoicePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class InvoicePaymentController {

    private final InvoicePaymentService invoicePaymentService;

    @PostMapping()
    public ResponseEntity<InvoicePaymentResponse> createPayment(@Valid @RequestBody InvoicePaymentRequest invoicePaymentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoicePaymentService.createPayment(invoicePaymentRequest));
    }

    @GetMapping()
    public ResponseEntity<List<InvoicePaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(invoicePaymentService.getAllPayments());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<InvoicePaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(invoicePaymentService.getPaymentById(id));
    }

    @GetMapping("/invoice")
    public ResponseEntity<List<InvoicePaymentResponse>> getPaymentsForInvoice(@RequestParam Long invoiceId) {
        return ResponseEntity.ok(invoicePaymentService.getPaymentsForInvoice(invoiceId));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<InvoicePaymentResponse>> getPaymentsForInvoicePath(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoicePaymentService.getPaymentsForInvoice(invoiceId));
    }

    @GetMapping("/business")
    public ResponseEntity<List<InvoicePaymentResponse>> getPaymentsForBusiness(@RequestParam Long businessId) {
        return ResponseEntity.ok(invoicePaymentService.getPaymentsForBusiness(businessId));
    }

    @GetMapping("/customer")
    public ResponseEntity<List<InvoicePaymentResponse>> getPaymentsForCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(invoicePaymentService.getPaymentsForCustomer(customerId));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        invoicePaymentService.deletePayment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

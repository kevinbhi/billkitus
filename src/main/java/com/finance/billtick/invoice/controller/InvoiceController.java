package com.finance.billtick.invoice.controller;

import com.finance.billtick.invoice.dto.InvoicePatchRequest;
import com.finance.billtick.invoice.dto.InvoiceRequest;
import com.finance.billtick.invoice.dto.InvoiceResponse;
import com.finance.billtick.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping()
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(invoiceRequest));
    }

    @GetMapping()
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

//    @PutMapping(value = "/{id}")
//    public ResponseEntity<InvoiceResponse> updateInvoice(@PathVariable Long id, @Valid @RequestBody InvoiceRequest invoiceRequest) {
//        return ResponseEntity.ok(invoiceService.updateInvoice(id, invoiceRequest));
//    }
//
//    @PatchMapping(value = "/{id}")
//    public ResponseEntity<InvoiceResponse> patchInvoice(@PathVariable Long id, @Valid @RequestBody InvoicePatchRequest invoicePatchRequest) {
//        return ResponseEntity.ok(invoiceService.patchInvoice(id, invoicePatchRequest));
//    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/business")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoicesForBusiness(@RequestParam Long businessId) {
        return ResponseEntity.ok(invoiceService.getAllInvoicesForBusiness(businessId));
    }

    @GetMapping("/customer")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoicesForCustomer(@RequestParam Long customerId) {
        return ResponseEntity.ok(invoiceService.getAllInvoicesForCustomer(customerId));
    }




}

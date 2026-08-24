package com.finance.billtick.payment.mapper;

import com.finance.billtick.payment.dto.InvoicePaymentRequest;
import com.finance.billtick.payment.dto.InvoicePaymentResponse;
import com.finance.billtick.payment.model.InvoicePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvoicePaymentMapper {

    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    InvoicePayment toInvoicePayment(InvoicePaymentRequest invoicePaymentRequest);

    @Mapping(source = "invoice.id", target = "invoiceId")
    @Mapping(source = "invoice.invoiceNumber", target = "invoiceNumber")
    @Mapping(source = "business.id", target = "businessId")
    @Mapping(source = "customer.id", target = "customerId")
    InvoicePaymentResponse toInvoicePaymentResponse(InvoicePayment invoicePayment);

    List<InvoicePaymentResponse> toInvoicePaymentResponseList(List<InvoicePayment> invoicePayments);
}

package com.finance.billtick.invoice.mapper;

import com.finance.billtick.invoice.dto.InvoiceItemRequest;
import com.finance.billtick.invoice.dto.InvoiceItemResponse;
import com.finance.billtick.invoice.dto.InvoicePatchRequest;
import com.finance.billtick.invoice.dto.InvoiceRequest;
import com.finance.billtick.invoice.dto.InvoiceResponse;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvoiceMapper {

    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "total", ignore = true)

    Invoice toInvoice(InvoiceRequest invoiceRequest);

    @Mapping(source = "business.id", target = "businessId")
    @Mapping(source = "customer.id", target = "customerId")

    InvoiceResponse toInvoiceResponse(Invoice invoice);

    List<InvoiceResponse> toInvoiceResponseList(List<Invoice> invoices);

    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "total", ignore = true)


    void updateInvoice(InvoiceRequest invoiceRequest, @MappingTarget Invoice invoice);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "taxAmount", ignore = true)
    @Mapping(target = "total", ignore = true)

    void patchInvoice(InvoicePatchRequest invoiceRequest, @MappingTarget Invoice invoice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "lineTax", ignore = true)
    InvoiceItem toInvoiceItem(InvoiceItemRequest invoiceItemRequest);

    @Mapping(source = "product.id", target = "productId")
    InvoiceItemResponse toInvoiceItemResponse(InvoiceItem invoiceItem);
}

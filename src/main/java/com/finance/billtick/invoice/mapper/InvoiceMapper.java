package com.finance.billtick.invoice.mapper;

import com.finance.billtick.invoice.dto.InvoiceItemRequest;
import com.finance.billtick.invoice.dto.InvoiceItemResponse;
import com.finance.billtick.invoice.dto.InvoiceResponse;
import com.finance.billtick.invoice.model.Invoice;
import com.finance.billtick.invoice.model.InvoiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvoiceMapper {

    @Mapping(source = "business.id", target = "businessId")
    @Mapping(source = "customer.id", target = "customerId")
    InvoiceResponse toInvoiceResponse(Invoice invoice);

    List<InvoiceResponse> toInvoiceResponseList(List<Invoice> invoices);

    // Copies only client-owned fields. unitPrice, totals, links and id are set by the service.
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

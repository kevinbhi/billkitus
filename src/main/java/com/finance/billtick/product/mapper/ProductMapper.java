package com.finance.billtick.product.mapper;

import com.finance.billtick.product.dto.ProductPatchRequest;
import com.finance.billtick.product.dto.ProductRequest;
import com.finance.billtick.product.dto.ProductResponse;
import com.finance.billtick.product.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    Product toProduct(ProductRequest productRequest);

    @Mapping(source = "business.id", target = "businessId")
    @Mapping(source = "customer.id", target = "customerId")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    void updateProduct(ProductRequest productRequest, @MappingTarget Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "business", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    void patchProduct(ProductPatchRequest productRequest, @MappingTarget Product product);
}

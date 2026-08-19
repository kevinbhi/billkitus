package com.finance.billtick.product.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.exception.DuplicateResourceException;
import com.finance.billtick.exception.InvalidPricingException;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.product.dto.ProductPatchRequest;
import com.finance.billtick.product.dto.ProductRequest;
import com.finance.billtick.product.dto.ProductResponse;
import com.finance.billtick.product.mapper.ProductMapper;
import com.finance.billtick.product.model.Product;
import com.finance.billtick.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
        product.setBusiness(assertBusiness(productRequest.getBusinessId()));
        assertUniqueProductCode(product.getBusiness(), product.getProductCode(), null);
        assertPricing(product.getPurchasePrice(), product.getSellingPrice());
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productMapper.toProductResponseList(productRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return productMapper.toProductResponse(assertProduct(id));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = assertProduct(id);
        Business business = assertBusiness(productRequest.getBusinessId());
        assertUniqueProductCode(business, productRequest.getProductCode(), id);
        productMapper.updateProduct(productRequest, product);
        product.setBusiness(business);
        assertPricing(product.getPurchasePrice(), product.getSellingPrice());
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse patchProduct(Long id, ProductPatchRequest productPatchRequest) {
        Product product = assertProduct(id);
        assertUniqueProductCode(product.getBusiness(), productPatchRequest.getProductCode(), id);
        productMapper.patchProduct(productPatchRequest, product);
        assertPricing(product.getPurchasePrice(), product.getSellingPrice());
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = assertProduct(id);
        productRepository.delete(product);
    }

    private Product assertProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    private void assertPricing(BigDecimal purchasePrice, BigDecimal sellingPrice) {
        if (sellingPrice.compareTo(purchasePrice) <= 0) {
            throw new InvalidPricingException("Selling price must be greater than purchase price");
        }
    }

    private void assertUniqueProductCode(Business business, String productCode, Long excludeId) {
        if (productCode == null) {
            return;
        }
        boolean exists = excludeId == null
                ? productRepository.existsByBusinessAndProductCode(business, productCode)
                : productRepository.existsByBusinessAndProductCodeAndIdNot(business, productCode, excludeId);
        if (exists) {
            throw new DuplicateResourceException("Product code already exists for this business: " + productCode);
        }
    }

    public List<ProductResponse> getAllProductsForBusiness(Long businessId) {
        return productMapper.toProductResponseList(productRepository.findByBusiness(assertBusiness(businessId)));
    }
}

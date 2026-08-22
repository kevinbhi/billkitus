package com.finance.billtick.product.service;

import com.finance.billtick.business.model.Business;
import com.finance.billtick.business.repository.BusinessRepository;
import com.finance.billtick.customer.model.Customer;
import com.finance.billtick.customer.repository.CustomerRepository;
import com.finance.billtick.exception.DuplicateResourceException;
import com.finance.billtick.exception.InvalidPricingException;
import com.finance.billtick.exception.InvalidRelationException;
import com.finance.billtick.exception.ResourceInUseException;
import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.invoice.repository.InvoiceRepository;
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
    private final CustomerRepository customerRepository;
//    private final InvoiceRepository invoiceRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = productMapper.toProduct(productRequest);
        product.setBusiness(assertBusiness(productRequest.getBusinessId()));
        assertUniqueProductCode(product.getBusiness(), product.getProductCode(), null);
        assertPricing(product.getPurchasePrice(), product.getSellingPrice());
        applyCustomer(product, productRequest.getCustomerId());
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
        applyCustomer(product, productRequest.getCustomerId());
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse patchProduct(Long id, ProductPatchRequest productPatchRequest) {
        Product product = assertProduct(id);
        assertUniqueProductCode(product.getBusiness(), productPatchRequest.getProductCode(), id);
        productMapper.patchProduct(productPatchRequest, product);
        assertPricing(product.getPurchasePrice(), product.getSellingPrice());
        if (productPatchRequest.getCustomerId() != null) {
            applyCustomer(product, productPatchRequest.getCustomerId());
        }
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = assertProduct(id);

//        if (invoiceRepository.existsByProduct(product)) {
//            throw new ResourceInUseException("Product with id: " + id
//                    + " cannot be deleted because one or more invoices reference it");
//        }
        product.setActive(false);
        productRepository.save(product);
    }

    private Product assertProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Business assertBusiness(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));
    }

    private Customer assertCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private void applyCustomer(Product product, Long customerId) {
        if (customerId == null) {
            product.setCustomer(null);
            return;
        }
        Customer customer = assertCustomer(customerId);
        assertCustomerBelongsToBusiness(product.getBusiness(), customer);
        product.setCustomer(customer);
    }

    private void assertCustomerBelongsToBusiness(Business business, Customer customer) {
        if (customer.getBusiness() == null || !customer.getBusiness().getId().equals(business.getId())) {
            throw new InvalidRelationException("Customer with id: " + customer.getId()
                    + " does not belong to business with id: " + business.getId());
        }
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

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProductsForCustomer(Long customerId) {
        return productMapper.toProductResponseList(productRepository.findByCustomer(assertCustomer(customerId)));
    }
}

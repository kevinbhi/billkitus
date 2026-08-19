package com.finance.billtick.product.controller;

import com.finance.billtick.product.dto.ProductPatchRequest;
import com.finance.billtick.product.dto.ProductRequest;
import com.finance.billtick.product.dto.ProductResponse;
import com.finance.billtick.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<ProductResponse> patchProduct(@PathVariable Long id, @Valid @RequestBody ProductPatchRequest productPatchRequest) {
        return ResponseEntity.ok(productService.patchProduct(id, productPatchRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/business")
    public ResponseEntity<List<ProductResponse>> getAllProductsForBusiness(@RequestParam Long businessId) {
        return ResponseEntity.ok(productService.getAllProductsForBusiness(businessId));
    }



}

package com.finance.billtick.product.repository;


import com.finance.billtick.business.model.Business;
import com.finance.billtick.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusiness(Business business);

    boolean existsByBusinessAndProductCode(Business business, String productCode);

    boolean existsByBusinessAndProductCodeAndIdNot(Business business, String productCode, Long id);
}
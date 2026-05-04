// product/repository/ProductRepository.java
package com.carinosas.juniorn8napi.product.repository;

import com.carinosas.juniorn8napi.product.domain.Product;
import com.carinosas.juniorn8napi.product.domain.ProductType;
import com.carinosas.juniorn8napi.product.domain.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Available products, sorted A→Z by name
    List<Product> findByStockGreaterThanOrderByNameAsc(int stock);

    // Flexible search (any combo of filters; nulls ignored)
    @Query("""
           SELECT p FROM Product p
           WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
             AND (:type IS NULL OR p.type = :type)
             AND (:size IS NULL OR p.size = :size)
           ORDER BY p.name ASC
                      
           """)
    List<Product> search(String name, ProductType type, Size size);
}
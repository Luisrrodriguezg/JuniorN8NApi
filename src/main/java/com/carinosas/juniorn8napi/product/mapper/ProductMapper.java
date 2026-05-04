// product/mapper/ProductMapper.java
package com.carinosas.juniorn8napi.product.mapper;

import com.carinosas.juniorn8napi.product.domain.Product;
import com.carinosas.juniorn8napi.product.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final int lowStockThreshold;
    private final String currency;

    public ProductMapper(
            @Value("${store.low-stock-threshold:20}") int lowStockThreshold,
            @Value("${store.currency:COP}") String currency) {
        this.lowStockThreshold = lowStockThreshold;
        this.currency = currency;
    }

    public ProductResponse toResponse(Product p) {
        boolean available = p.getStock() > 0;
        boolean lowStock  = available && p.getStock() < lowStockThreshold;
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getType(),
                p.getSize(),
                p.getPrice(),
                currency,
                p.getStock(),
                available,
                lowStock
        );
    }
}
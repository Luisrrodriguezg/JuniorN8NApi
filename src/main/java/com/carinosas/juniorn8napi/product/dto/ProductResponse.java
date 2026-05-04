package com.carinosas.juniorn8napi.product.dto;

import com.carinosas.juniorn8napi.product.domain.ProductType;
import com.carinosas.juniorn8napi.product.domain.Size;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        ProductType type,
        Size size,
        BigDecimal price,
        String currency,
        Integer stock,
        boolean available,
        boolean lowStock
) {}
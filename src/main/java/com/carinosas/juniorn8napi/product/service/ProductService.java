// product/service/ProductService.java
package com.carinosas.juniorn8napi.product.service;

import com.carinosas.juniorn8napi.product.domain.ProductType;
import com.carinosas.juniorn8napi.product.domain.Size;
import com.carinosas.juniorn8napi.product.dto.ProductResponse;
import com.carinosas.juniorn8napi.product.exception.ProductNotFoundException;
import com.carinosas.juniorn8napi.product.mapper.ProductMapper;
import com.carinosas.juniorn8napi.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ProductResponse> getCatalog() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(p -> p.getName().toLowerCase()))
                .map(mapper::toResponse)
                .toList();
    }

    public List<ProductResponse> getAvailable() {
        return repository.findByStockGreaterThanOrderByNameAsc(0).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ProductResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<ProductResponse> search(String name, ProductType type, Size size) {
        return repository.search(name, type, size).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
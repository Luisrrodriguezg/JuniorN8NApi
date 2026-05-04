// product/controller/ProductController.java
package com.carinosas.juniorn8napi.product.controller;

import com.carinosas.juniorn8napi.product.domain.ProductType;
import com.carinosas.juniorn8napi.product.domain.Size;
import com.carinosas.juniorn8napi.product.dto.ProductResponse;
import com.carinosas.juniorn8napi.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> getCatalog() {
        return service.getCatalog();
    }

    @GetMapping("/available")
    public List<ProductResponse> getAvailable() {
        return service.getAvailable();
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/search")
    public List<ProductResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) Size size) {
        return service.search(name, type, size);
    }
}
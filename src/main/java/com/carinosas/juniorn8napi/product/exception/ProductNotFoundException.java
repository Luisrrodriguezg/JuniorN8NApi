// product/exception/ProductNotFoundException.java
package com.carinosas.juniorn8napi.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found: id=" + id);
    }
}
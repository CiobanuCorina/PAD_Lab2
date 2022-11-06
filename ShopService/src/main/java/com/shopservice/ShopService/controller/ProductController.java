package com.shopservice.ShopService.controller;

import com.shopservice.ShopService.exception.ResourceNotFoundException;
import com.shopservice.ShopService.model.Product;
import com.shopservice.ShopService.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    @Autowired
    private ServerProperties serverProperties;


    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String getAllProducts() throws InterruptedException {
        return serverProperties.getPort() + productRepository.findAll().toString();
    }

    @PostMapping("/")
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(value = "id") long productId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product for this id" + productId));
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id") long productId,
                                 @RequestBody Product productDetails) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product for this id" + productId));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());

        productRepository.save(product);

        return ResponseEntity.ok().body(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") long productId) throws ResourceNotFoundException {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product for this id" + productId));

        productRepository.deleteById(productId);
        return ResponseEntity.ok().build();
    }
}

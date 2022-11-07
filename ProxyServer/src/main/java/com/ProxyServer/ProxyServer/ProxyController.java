package com.ProxyServer.ProxyServer;

import com.ProxyServer.ProxyServer.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class ProxyController {
    @Autowired
    RestTemplate template;

    @GetMapping("/shop-service/products")
    @ResponseBody
    public String invokeShopService() {
        return template.getForObject("http://SHOP-SERVICE/api/v1/product/", String.class);
    }

    @PostMapping("/shop-service/products")
    @ResponseBody
    public String createProduct(@RequestBody Product product) {
        return template.postForObject("http://SHOP-SERVICE/api/v1/product/", product, String.class);
    }

    @PutMapping("/shop-service/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id") UUID productId,
                                @RequestBody Product productDetails) {
        HttpEntity<Product> request = new HttpEntity<Product>(productDetails);
        return template.exchange(
                "http://SHOP-SERVICE/api/v1/product/" + productId,
                HttpMethod.PUT,
                request,
                Product.class);
    }

    @DeleteMapping(("/shop-service/products/{id}"))
    public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") UUID productId) {
        return template.exchange( "http://SHOP-SERVICE/api/v1/product/" + productId,
                HttpMethod.DELETE,
                null, String.class);
    }
}

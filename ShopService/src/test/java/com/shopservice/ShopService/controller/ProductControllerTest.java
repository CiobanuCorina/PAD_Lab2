package com.shopservice.ShopService.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopservice.ShopService.ShopServiceApplication;
import com.shopservice.ShopService.model.Product;
import com.shopservice.ShopService.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private ProductRepository repoMock;

    @InjectMocks
    private ProductController productController;

    private final UUID uuid = new UUID(12,30);
    private final Product product1 = new Product(uuid,"Test", 20.34,3);
    private final Product product2 = new Product(new UUID(13, 25), "Test2", 23.56, 2);
    private final List<Product> productList = new ArrayList<>(){{add(product1); add(product2);}};

    @Before
    public void setUp() {
        when(repoMock.findAll())
                .thenReturn(productList);
        when(repoMock.findById(uuid))
                .thenReturn(Optional.of(product1));
        when(repoMock.save(Mockito.any(Product.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        mvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void getProductList() throws Exception {
        MvcResult result = mvc.perform(get("/api/v1/product/"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        List<Product> actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(productList, actual);
    }

    @Test
    public void createProduct() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(product1);
        mvc.perform(post("/api/v1/product/")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateProduct() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String newName = "Test2";
        double newPrice = 23.15;
        int newQuantity = 2;
        Product updatedProduct = new Product(newName, newPrice, newQuantity);
        Product updatedExpected = new Product(uuid, newName, newPrice, newQuantity);
        String jsonString = mapper.writeValueAsString(updatedProduct);
        MvcResult result = mvc.perform(put("/api/v1/product/{id}", uuid)
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Product actual = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(updatedExpected, actual);
    }

    @Test
    public void updateProductError() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String newName = "Test2";
        double newPrice = 23.15;
        int newQuantity = 2;
        Product updatedProduct = new Product(newName, newPrice, newQuantity);
        String jsonString = mapper.writeValueAsString(updatedProduct);
        mvc.perform(put("/api/v1/product/nonexistent-id")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteProduct() throws Exception {
        mvc.perform(delete("/api/v1/product/{id}", uuid))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteProductError() throws Exception {
        mvc.perform(delete("/api/v1/product/nonexistent-id"))
                .andExpect(status().is4xxClientError());
    }
}

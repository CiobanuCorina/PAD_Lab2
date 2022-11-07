package com.ProxyServer.ProxyServer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private double price;
    private int quantity;
}

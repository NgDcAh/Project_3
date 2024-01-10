package com.onlineshop.dto;


import com.onlineshop.dto.ProductDto;
import com.onlineshop.dto.ShoppingCartDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

	private Long id;

    private ShoppingCartDto cart;

    private ProductDto product;

    private int quantity;

    private double unitPrice;
}

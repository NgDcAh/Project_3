package com.onlineshop.dto;

import java.util.HashSet;
import java.util.Set;

import com.onlineshop.model.User;
import com.onlineshop.dto.CartItemDto;
import com.onlineshop.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {
	private Long id;

    private User customer;

    private double totalPrice;

    private int totalItems;

    private Set<CartItemDto> cartItems = new HashSet<>();
}

package com.onlineshop.service;

import java.util.Collection;
import java.util.Set;

import com.onlineshop.dto.ProductDto;
import com.onlineshop.dto.ShoppingCartDto;
import com.onlineshop.model.CartItem;
import com.onlineshop.model.Product;


public interface ShoppingCartService {

	ShoppingCartDto addItemToCartSession(ShoppingCartDto cartDto, ProductDto productDto, int quantity);

	ShoppingCartDto updateCartSession(ShoppingCartDto cartDto, ProductDto productDto, int quantity);

	ShoppingCartDto removeItemFromCartSession(ShoppingCartDto cartDto, ProductDto productDto);
	
}

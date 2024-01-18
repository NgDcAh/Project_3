package com.onlineshop.service;

import java.util.List;

import com.onlineshop.dto.ShoppingCartDto;
import com.onlineshop.model.Order;
import com.onlineshop.model.User;

public interface OrderService {
	Order save(User user, ShoppingCartDto shoppingCartDto, String paymentMethod);

    List<Order> findAll(String username);

    List<Order> findALlOrders();

    Order acceptOrder(Long id);

    void cancelOrder(Long id);
}

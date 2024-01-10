package com.onlineshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlineshop.dto.CartItemDto;
import com.onlineshop.dto.ProductDto;
import com.onlineshop.dto.ShoppingCartDto;
import com.onlineshop.dto.UserDto;
import com.onlineshop.model.CartItem;
import com.onlineshop.model.User;
import com.onlineshop.model.Order;
import com.onlineshop.model.OrderDetail;
import com.onlineshop.model.Product;
import com.onlineshop.repository.UserRepository;
import com.onlineshop.repository.OrderDetailRepository;
import com.onlineshop.repository.OrderRepository;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.service.OrderService;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderDetailRepository detailRepository;

	@Autowired
	private UserRepository customerRepository;

	@Autowired
	private ShoppingCartServiceImpl cartServiceImpl;
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	@Transactional
	public Order save(User user, ShoppingCartDto shoppingCartDto) {
		Order order = new Order();
		order.setOrderDate(new Date());
		order.setUser(user);
		order.setTax(2);
		order.setTotalPrice(shoppingCartDto.getTotalPrice());
		order.setAccept(false);
		order.setPaymentMethod("Cash");
		order.setOrderStatus("Successed");
		order.setQuantity(shoppingCartDto.getTotalItems());
		List<OrderDetail> orderDetailList = new ArrayList<>();
		for (CartItemDto item : shoppingCartDto.getCartItems()) {
			Product product = transfer(item.getProduct());
				OrderDetail orderDetail = new OrderDetail();
				orderDetail.setOrder(order);
				orderDetail.setProduct(product);
				orderDetail.setQuantity(item.getQuantity());
				if((product.getCurrentQuantity() - item.getQuantity()) >= 0 ) {
					int quantity = product.getCurrentQuantity() - item.getQuantity();
					productRepository.updateQuantity(quantity, product.getId());
					detailRepository.save(orderDetail);
					orderDetailList.add(orderDetail);
				}
				else {
					return null;
				}
		}
		order.setOrderDetailList(orderDetailList);
		cartServiceImpl.clearItems(shoppingCartDto);
		return orderRepository.save(order);
	}
	

	@Override
	public List<Order> findAll(String username) {
		User customer = customerRepository.findByUsername(username);
		List<Order> orders = customer.getOrders();
		return orders;
	}

	@Override
	public List<Order> findALlOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Order acceptOrder(Long id) {
		Order order = orderRepository.findById(id).orElse(null);
		order.setAccept(true);
		order.setDeliveryDate(new Date());
		return orderRepository.save(order);
	}

	@Override
	public void cancelOrder(Long id) {
		orderRepository.deleteById(id);
	}
	
	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public void removeOrderById(Long id) {
		orderRepository.deleteOrderDetailByOrderId(id);
		orderRepository.deleteOrderById(id);
	}
	
	
	public double getInvestment() {
		Double investment = orderRepository.getInvestment();
		if (investment == null) {
			return 0.0;
		}
	    return investment;
	}
	
	public double getRevenue() {
		Double revenue = orderRepository.getRevenue();
		if (revenue == null) {
			return 0.0;
		}
	    return revenue;
	}
	
	public int getTotalOrders() {
		Integer orderQuantity = orderRepository.getOrderQuantity();
		if (orderQuantity == null) {
			return 0;
		}
		return orderQuantity;
	}
	
	public int getTotalSoldProducts() {
		Integer totalSoldProducts = orderRepository.getTotalSoldProducts();
		if (totalSoldProducts == null) {
			return 0;
		}
		return totalSoldProducts;
	}
	
	public List<Double> getProfitPerMonth(){
		List<Double> profitPerMonthDoubles = new ArrayList<>();
		Integer year = 2024;
		for(Integer i = 1; i <= 12; i++) {
			Double revenue = orderRepository.getRevenueByMonthAndYear(i, year);
			Double investment = orderRepository.getInvestmentByMonthAndYear(i, year);
			if(revenue == null) {
				if(investment == null) {
					profitPerMonthDoubles.add(0.0);
				}
				else {
					profitPerMonthDoubles.add(0.0 - investment);
				}
			}
			else {
				profitPerMonthDoubles.add(revenue - investment);
			}
			
		}
		return profitPerMonthDoubles;
	}

	private Product transfer(ProductDto productDto) {
		Product product = new Product();
		product.setId(productDto.getId());
		product.setName(productDto.getName());
		product.setCurrentQuantity(productDto.getCurrentQuantity());
		product.setCostPrice(productDto.getCostPrice());
		product.setSalePrice(productDto.getSalePrice());
		product.setDescription(productDto.getDescription());
		product.setImage(productDto.getImage());
		product.set_activated(productDto.isActivated());
		product.set_deleted(productDto.isDeleted());
		product.setCategory(productDto.getCategory());
		return product;
	}
	
	public List<Integer> getQuantityOrderByMonthAndYear(){
		List<Integer> quantityOrder = new ArrayList<>();
		Integer year = 2024;
		for(Integer i = 1; i <= 12; i++) {
			Integer quantity = orderRepository.getQuantityOrderByMonthAndYear(i, year);
			if(quantity == null) {
				quantityOrder.add(0);
			}
			else {
				quantityOrder.add(quantity);
			}
			
		}
		return quantityOrder;
	}
	
	public List<Order> findOrdersByFilters(LocalDate startDate, LocalDate endDate, Double minTotalPrice, Double maxTotalPrice, Integer minQuantity, Integer maxQuantity) {
		return orderRepository.findOrdersByFilters(startDate, endDate, minTotalPrice, maxTotalPrice, minQuantity, maxQuantity);
	}
	
	public Map<User, Double> getTop5Customers() {
		List<Map<String, Object>> result = customerRepository.getTop5Customer();
		Map<User, Double> resultMap = new LinkedHashMap<>();
		for (Map<String, Object> entry : result) {
			User user = customerRepository.findById((Long) entry.get("user_id")).orElse(null);
			
	        Double price = (Double) entry.get("total_order_price");

	        resultMap.put(user, price);
		}
		return resultMap;
	}
	
}

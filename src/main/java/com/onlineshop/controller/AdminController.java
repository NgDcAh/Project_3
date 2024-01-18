package com.onlineshop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.onlineshop.model.Category;
import com.onlineshop.model.Product;
import com.onlineshop.model.User;
import com.onlineshop.service.impl.CategoryServiceImpl;
import com.onlineshop.service.impl.InputServiceImpl;
import com.onlineshop.service.impl.OrderServiceImpl;
import com.onlineshop.service.impl.ProductServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;

@Controller
public class AdminController {
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	@Autowired
	private CategoryServiceImpl categoryServiceImpl;
	
	@Autowired
	private ProductServiceImpl productServiceImpl;
	
	@Autowired
	private InputServiceImpl inputServiceImpl;
	

	@GetMapping("/admin/get-profit-per-month")
	@ResponseBody
	public List<Double> getProfitPerMonth(){
		return orderServiceImpl.getProfitPerMonth();
	}
	
	@GetMapping("/admin/get-percentage-per-category")
	@ResponseBody
	public Map<String, Double> getPercentageCategory() {
		return categoryServiceImpl.getPercentageSoldByCategory();
	}
	
	@GetMapping("/admin/get-quantity-sold-product")
	@ResponseBody
	public List<Integer> getQuantitySoldProduct() {
		return productServiceImpl.getQuantitySoldProductByMonthAndYear();
	}
	
	@GetMapping("/admin/get-quantity-order")
	@ResponseBody
	public List<Integer> getQuantityOrderByMonth() {
		return orderServiceImpl.getQuantityOrderByMonthAndYear();
	}
	
	@GetMapping("/admin/get-quantity-product-import")
	@ResponseBody
	public List<Integer> getQuantityProductImportByMonth() {
		return inputServiceImpl.getQuantityProductImportByMonthAndYear();
	}
	
	
	@RequestMapping("/admin/index")
    public String index(Model model) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        Map<Product, Integer> top8Products = productServiceImpl.getTop8Products();
        Map<User, Double> top5Customers = orderServiceImpl.getTop5Customers();
        double investment = orderServiceImpl.getInvestment();
        double revenue = orderServiceImpl.getRevenue();
        int totalOrders = orderServiceImpl.getTotalOrders();
        int totalSoldProducts = orderServiceImpl.getTotalSoldProducts();
        int totalInventory = productServiceImpl.getTotalInventory();
        int qntOrderRequest = orderServiceImpl.getQuantityOrderRequest();
        int qntOrderDelivery = orderServiceImpl.getQuantityOrderDelivery();
        model.addAttribute("top8Products", top8Products);
        model.addAttribute("top5Customers", top5Customers);
        model.addAttribute("investment", investment);
        model.addAttribute("revenue", revenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSoldProducts", totalSoldProducts);
        model.addAttribute("totalInventory", totalInventory);
        model.addAttribute("qntOrderRequest", qntOrderRequest);
        model.addAttribute("qntOrderDelivery", qntOrderDelivery);
        return "index";
    }
	
}

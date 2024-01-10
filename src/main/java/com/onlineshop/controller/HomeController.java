package com.onlineshop.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.onlineshop.dto.ProductDto;
import com.onlineshop.dto.ShoppingCartDto;
import com.onlineshop.model.User;
import com.onlineshop.service.impl.UserServiceImpl;
import com.onlineshop.service.impl.ProductServiceImpl;
import com.onlineshop.service.impl.ShoppingCartServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	private UserServiceImpl customerServiceImpl;
	
	@Autowired 
	private ProductServiceImpl productServiceImpl;
	
	@Autowired
	private ShoppingCartServiceImpl cartServiceImpl;
	
	@RequestMapping("/")
	public String index(Model model) {            
	    return "redirect:/home";
	}
	

	@RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(Model model, Principal principal, HttpSession session) {
        model.addAttribute("title", "Home");
        model.addAttribute("page", "Home");
        List<ProductDto> products = productServiceImpl.getTrendingProduct();
        model.addAttribute("products", products);
        if (principal != null) {
            User user = customerServiceImpl.findByUsername(principal.getName());
            session.setAttribute("username", user.getFirstName() + " " + user.getLastName());
            ShoppingCartDto shoppingCartDto= (ShoppingCartDto) session.getAttribute("shoppingCart");
            
            
            if (shoppingCartDto != null) {
                session.setAttribute("totalItems", shoppingCartDto.getTotalItems());
            }
        }
        return "home";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Contact");
        model.addAttribute("page", "Contact");
        return "contact-us";
    }
}

package com.onlineshop.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.onlineshop.model.User;
import com.onlineshop.service.impl.UserServiceImpl;
import com.onlineshop.dto.ProductDto;
import com.onlineshop.dto.ShoppingCartDto;
import com.onlineshop.model.User;
import com.onlineshop.service.impl.UserServiceImpl;
import com.onlineshop.service.impl.ProductServiceImpl;
import com.onlineshop.service.impl.ShoppingCartServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("shoppingCart")
public class CartController {
	@Autowired
	private ShoppingCartServiceImpl cartServiceImpl;

	@Autowired
	private ProductServiceImpl productServiceImpl;

	@Autowired
	private UserServiceImpl customerServiceImpl;

	@GetMapping("/customer/cart")
	public String cart(Model model, Principal principal, HttpSession session) {
		if (principal == null) {
			return "redirect:/login";
		} else {
			User customer = customerServiceImpl.findByUsername(principal.getName());
			ShoppingCartDto shoppingCartDto = (ShoppingCartDto) session.getAttribute("shoppingCart");

			if (shoppingCartDto == null) {
				model.addAttribute("check");

			}
			if (shoppingCartDto != null) {
				model.addAttribute("grandTotal", shoppingCartDto.getTotalPrice());
				session.setAttribute("totalItems", shoppingCartDto.getTotalItems());
			}
			session.setAttribute("shoppingCart", shoppingCartDto);
			model.addAttribute("title", "Cart");
			return "cart";
		}

	}

	@RequestMapping("/customer/add-to-cart")
	public String addItemToCart(@RequestParam("id") Long id,
			@RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
			HttpServletRequest request, Model model, Principal principal, RedirectAttributes redirectAttributes,
			HttpSession session) {

		ProductDto productDto = productServiceImpl.getById(id);
		if (principal == null) {
			return "redirect:/login";
		} else {

			String username = principal.getName();
			ShoppingCartDto shoppingCartDto = (ShoppingCartDto) session.getAttribute("shoppingCart");
			
			if (cartServiceImpl.checkEnough(productDto, quantity)) {
				shoppingCartDto = cartServiceImpl.addItemToCartSession(shoppingCartDto, productDto, quantity);
				session.setAttribute("totalItems", shoppingCartDto.getTotalItems());
				session.setAttribute("shoppingCart", shoppingCartDto);
				return "redirect:" + request.getHeader("Referer");
			} else {
				if (shoppingCartDto == null) {
				    shoppingCartDto = new ShoppingCartDto(); 
				}
				redirectAttributes.addFlashAttribute("failed", "This product is not enough");
				session.setAttribute("totalItems", shoppingCartDto.getTotalItems());
				session.setAttribute("shoppingCart", shoppingCartDto);
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/customer/update-cart-product", method = RequestMethod.POST)
	public String updateCart(@RequestParam("id") Long id, @RequestParam("quantity") int quantity,
			HttpServletRequest request, Model model, Principal principal, HttpSession session, RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/login";
		} else {
			ProductDto productDto = productServiceImpl.getById(id);
			String username = principal.getName();
			ShoppingCartDto shoppingCartDto = (ShoppingCartDto) session.getAttribute("shoppingCart");
			if (cartServiceImpl.checkEnough(productDto, quantity)) {
				shoppingCartDto = cartServiceImpl.updateCartSession(shoppingCartDto, productDto, quantity);
				session.setAttribute("shoppingCart", shoppingCartDto);
				session.setAttribute("totalItems", shoppingCartDto.getTotalItems());
				return "redirect:" + request.getHeader("Referer");
			}
			else {
				redirectAttributes.addFlashAttribute("failed", "This product is not enough");
				session.setAttribute("totalItems", shoppingCartDto.getTotalItems());
				session.setAttribute("shoppingCart", shoppingCartDto);
			}
			
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/customer/delete-cart-product", method = RequestMethod.GET)
	public String deleteItem(@RequestParam("id") Long id, Model model, Principal principal, HttpSession session) {
		if (principal == null) {
			return "redirect:/login";
		} else {
			ProductDto productDto = productServiceImpl.getById(id);
			String username = principal.getName();
			ShoppingCartDto shoppingCartDto = (ShoppingCartDto) session.getAttribute("shoppingCart");
			shoppingCartDto = cartServiceImpl.removeItemFromCartSession(shoppingCartDto, productDto);
			session.setAttribute("shoppingCart", shoppingCartDto);
			return "redirect:/customer/cart";
		}
	}
}

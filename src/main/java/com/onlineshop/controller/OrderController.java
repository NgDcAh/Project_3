package com.onlineshop.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.onlineshop.dto.CartItemDto;
import com.onlineshop.dto.ProductDto;
import com.onlineshop.dto.ShoppingCartDto;
import com.onlineshop.dto.UserDto;
import com.onlineshop.model.Category;
import com.onlineshop.model.City;
import com.onlineshop.model.Country;
import com.onlineshop.model.User;
import com.onlineshop.repository.OrderRepository;
import com.onlineshop.model.Order;
import com.onlineshop.model.OrderDetail;
import com.onlineshop.model.Product;
import com.onlineshop.service.impl.CityServiceImpl;
import com.onlineshop.service.impl.CountryServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;
import com.onlineshop.service.impl.OrderServiceImpl;
import com.onlineshop.service.impl.ShoppingCartServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	@Autowired
	private UserServiceImpl customerServiceImpl;
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	@Autowired
	private ShoppingCartServiceImpl cartServiceImpl;
	@Autowired
	private CountryServiceImpl countryServiceImpl;
	@Autowired
	private CityServiceImpl cityServiceImpl;

	@GetMapping("/customer/check-out")
	public String checkOut(Principal principal, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
		if (principal == null) {
			return "redirect:/login";
		} else {
			UserDto customer = customerServiceImpl.getUserDto(principal.getName());
			if (customer.getAddress() == null || customer.getCity() == null || customer.getPhoneNumber() == null) {
				model.addAttribute("information", "Bạn cần cập nhật thông tin trước khi thanh toán");
				List<Country> countryList = countryServiceImpl.findAll();
				List<City> cities = cityServiceImpl.findAll();
				model.addAttribute("customer", customer);
				model.addAttribute("cities", cities);
				model.addAttribute("countries", countryList);
				model.addAttribute("title", "Profile");
				model.addAttribute("page", "Profile");
				return "profile";
			} else {
				ShoppingCartDto shoppingCartDto = (ShoppingCartDto) session.getAttribute("shoppingCart");
				
				if (shoppingCartDto != null && !shoppingCartDto.getCartItems().isEmpty()) {
					Set<CartItemDto> cartItemDtos = shoppingCartDto.getCartItems();
					model.addAttribute("customer", customer);
					model.addAttribute("title", "Check-Out");
					model.addAttribute("page", "Check-Out");
					session.setAttribute("shoppingCart", shoppingCartDto);
					model.addAttribute("grandTotal", shoppingCartDto.getTotalPrice());
					return "checkout";
				}
				else {
					redirectAttributes.addFlashAttribute("error", "Giỏ của bạn không có gì!");
					return "redirect:/customer/cart";
				}
			}
		}
	}

	@GetMapping("/customer/orders")
	public String getOrders(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		} else {
			User customer = customerServiceImpl.findByUsername(principal.getName());
			List<Order> orderList = customer.getOrders();
			model.addAttribute("orders", orderList);
			model.addAttribute("title", "Order");
			model.addAttribute("page", "Order");
			return "order";
		}
	}

	@RequestMapping(value = "/customer/order-detail", method = { RequestMethod.PUT, RequestMethod.GET })
	public String showOrderDetail(@RequestParam("id") Long id, RedirectAttributes attributes, Model model) {
		Order order = orderServiceImpl.getOrderById(id);
		model.addAttribute("order", order);
		return "order-detail";
	}

	@RequestMapping(value = "/customer/add-order", method = { RequestMethod.POST })
	public String createOrder(Principal principal, Model model, HttpSession session,
			RedirectAttributes redirectAttributes, @RequestParam(name = "paymentMethod", required = true) String paymentMethod) {
		if (principal == null) {
			return "redirect:/login";
		} else {
			User customer = customerServiceImpl.findByUsername(principal.getName());
			ShoppingCartDto shoppingCartDto = (ShoppingCartDto) session.getAttribute("shoppingCart");
			Order order = orderServiceImpl.save(customer, shoppingCartDto, paymentMethod);
			if (order != null) {
				session.removeAttribute("shoppingCart");
				session.removeAttribute("totalItems");
				model.addAttribute("order", order);
				model.addAttribute("title", "Order Detail");
				model.addAttribute("page", "Order Detail");
				model.addAttribute("success", "Thanh toán thành công");
				return "order-detail";
			} else {
				model.addAttribute("title", "Order Detail");
				model.addAttribute("page", "Order Detail");
				redirectAttributes.addAttribute("error", "Sản phẩm này không đủ số lượng!");
				return "cart";
			}
		}
	}

	@GetMapping("/admin/list-order")
	public String orders(Model model) {
		List<Order> orders = orderServiceImpl.findALlOrders();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order";
	}
	
	@GetMapping("/admin/list-order-request")
	public String orderRequest(Model model) {
		List<Order> orders = orderServiceImpl.getAllOrderRequest();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order-request";
	}
	
	@GetMapping("/admin/list-order-delivery")
	public String orderDelivery(Model model) {
		List<Order> orders = orderServiceImpl.getAllOrderDevery();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order-delivery";
	}
	
	@GetMapping("/admin/list-order-reject")
	public String orderReject(Model model) {
		List<Order> orders = orderServiceImpl.getAllOrderReject();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order-reject";
	}
	
	@GetMapping("/admin/accept-order")
	public String acceptOrder(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		orderServiceImpl.acceptOrder(id);
		List<Order> orders = orderServiceImpl.getAllOrderRequest();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		redirectAttributes.addFlashAttribute("success", "Xác nhận thành công!");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "redirect:list-order-request";
	}
	
	@GetMapping("/admin/complete-order")
	public String completeOrderByAdmin(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		orderServiceImpl.completeOrder(id);
		List<Order> orders = orderServiceImpl.getAllOrderDevery();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		redirectAttributes.addFlashAttribute("success", "Giao hàng thành công!");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "redirect:list-order-delivery";
	}
	
	@GetMapping("/customer/complete-order")
	public String completeOrderByCustomer(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		orderServiceImpl.completeOrder(id);
		List<Order> orders = orderServiceImpl.getAllOrderDevery();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã mua hàng!");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "redirect:orders";
	}
	
	@GetMapping({"/admin/reject-order"})
	public String rejectOrderByAdmin(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		orderServiceImpl.rejectOrder(id);
		List<Order> orders = orderServiceImpl.getAllOrderReject();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		redirectAttributes.addFlashAttribute("success", "Đã từ chối!");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "redirect:list-order-reject";
	}
	
	@GetMapping({"/customer/reject-order"})
	public String rejectOrderByCustomer(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		orderServiceImpl.rejectOrder(id);
		List<Order> orders = orderServiceImpl.getAllOrderReject();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng!");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "redirect:orders";
	}
	
	@GetMapping({"/admin/reject-order-delivery"})
	public String rejectOrderDeliveryByAdmin(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		orderServiceImpl.rejectOrder(id);
		List<Order> orders = orderServiceImpl.getAllOrderReject();
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng!");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "redirect:list-order-delivery";
	}

	@GetMapping("/admin/filtered-orders")
	public String orderFilter(
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(name = "minTotalPrice", required = false) Double minTotalPrice,
			@RequestParam(name = "maxTotalPrice", required = false) Double maxTotalPrice,
			@RequestParam(name = "minQuantity", required = false) Integer minQuantity,
			@RequestParam(name = "maxQuantity", required = false) Integer maxQuantity, Model model) {
		List<Order> orders = orderServiceImpl.findOrdersByFilters(startDate, endDate, minTotalPrice, maxTotalPrice,
				minQuantity, maxQuantity);
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		model.addAttribute("result", "Kết quả: ");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order";
	}
	
	@GetMapping("/admin/filtered-orders-request")
	public String orderRequestFilter(
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(name = "minTotalPrice", required = false) Double minTotalPrice,
			@RequestParam(name = "maxTotalPrice", required = false) Double maxTotalPrice,
			@RequestParam(name = "minQuantity", required = false) Integer minQuantity,
			@RequestParam(name = "maxQuantity", required = false) Integer maxQuantity, Model model) {
		List<Order> orders = orderServiceImpl.findOrdersRequestByFilters(startDate, endDate, minTotalPrice, maxTotalPrice,
				minQuantity, maxQuantity);
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		model.addAttribute("result", "Kết quả: ");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order-request";
	}
	
	@GetMapping("/admin/filtered-orders-delivery")
	public String orderDeliveryFilter(
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(name = "minTotalPrice", required = false) Double minTotalPrice,
			@RequestParam(name = "maxTotalPrice", required = false) Double maxTotalPrice,
			@RequestParam(name = "minQuantity", required = false) Integer minQuantity,
			@RequestParam(name = "maxQuantity", required = false) Integer maxQuantity, Model model) {
		List<Order> orders = orderServiceImpl.findOrdersDeliveryByFilters(startDate, endDate, minTotalPrice, maxTotalPrice,
				minQuantity, maxQuantity);
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		model.addAttribute("result", "Kết quả: ");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order-delivery";
	}
	
	@GetMapping("/admin/filtered-orders-reject")
	public String orderRejectFilter(
			@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(name = "minTotalPrice", required = false) Double minTotalPrice,
			@RequestParam(name = "maxTotalPrice", required = false) Double maxTotalPrice,
			@RequestParam(name = "minQuantity", required = false) Integer minQuantity,
			@RequestParam(name = "maxQuantity", required = false) Integer maxQuantity, Model model) {
		List<Order> orders = orderServiceImpl.findOrdersRejectByFilters(startDate, endDate, minTotalPrice, maxTotalPrice,
				minQuantity, maxQuantity);
		model.addAttribute("orders", orders);
		model.addAttribute("size", orders.size());
		model.addAttribute("result", "Kết quả: ");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-order-reject";
	}

	@RequestMapping(value = "/admin/findOrderById", method = { RequestMethod.PUT, RequestMethod.GET })
	@ResponseBody
	public Order findById(Long id) {
		return orderServiceImpl.getOrderById(id);
	}
	
	@GetMapping(value = "/admin/remove-order")
	public String removeOrderString(Long id, RedirectAttributes redirectAttributes) {
		try {
			orderServiceImpl.removeOrderById(id);
			redirectAttributes.addFlashAttribute("success", "Xóa thành công!");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Deleted failed!");
		}
		return "redirect:/admin/list-order";
	}

	@GetMapping("/admin/update-order/{id}")
	public String updateStaffForm(@PathVariable("id") Long id, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		Order order = orderServiceImpl.getOrderById(id);
		model.addAttribute("orderDto", order);
		return "update-order";
	}
}

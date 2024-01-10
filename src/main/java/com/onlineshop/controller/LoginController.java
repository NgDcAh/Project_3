package com.onlineshop.controller;

import java.awt.Dialog.ModalExclusionType;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.onlineshop.dto.UserDto;
import com.onlineshop.model.User;
import com.onlineshop.service.impl.OrderServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	

	@RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
	    HttpSession session = request.getSession(false);
	    if (session != null) {
	        session.invalidate();
	    }
	    return "redirect:/login";
	}
	
	@RequestMapping("/staff/index")
    public String staffIndex(Model model) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "index";
    }
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("userDto", new UserDto());
		return "register";
	}
	
	@GetMapping("/forgot-password")
	public String forgotPassword(Model model) {
		return "forgot-password";
	}
	
	@PostMapping("/register-new")
    public String addNewCustomer(@Valid @ModelAttribute("userDto") UserDto userDto,
                              BindingResult result,
                              Model model) {

        try {

            if (result.hasErrors()) {
                model.addAttribute("userDto", userDto);
                result.toString();
                return "register";
            }
            String username = userDto.getUsername();
            User customer = userServiceImpl.findByUsername(username);
            if (customer != null) {
                model.addAttribute("userDto", userDto);
                System.out.println("customer not null");
                model.addAttribute("emailError", "Your email has been registered!");
                return "register";
            }
            if (userDto.getPassword().equals(userDto.getConfirmPassword())) {
            	userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
                userServiceImpl.saveCustomer(userDto);
                System.out.println("success");
                model.addAttribute("success", "Register successfully!");
                model.addAttribute("userDto", userDto);
            } else {
                model.addAttribute("userDto", userDto);
                model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
                System.out.println("password not same");
                return "register";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errors", "The server has been wrong!");
        }
        return "login";

    }
}

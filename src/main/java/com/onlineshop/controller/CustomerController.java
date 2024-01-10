package com.onlineshop.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.onlineshop.dto.UserDto;
import com.onlineshop.model.City;
import com.onlineshop.model.Country;
import com.onlineshop.model.User;
import com.onlineshop.service.impl.CityServiceImpl;
import com.onlineshop.service.impl.CountryServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;

import jakarta.validation.Valid;

@Controller
public class CustomerController {
	@Autowired
	private UserServiceImpl customerServiceImpl;
	
	@Autowired
    private CountryServiceImpl countryServiceImpl;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private CityServiceImpl cityServiceImpl;

    @GetMapping("/customer/profile")
    public String profile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String username = principal.getName();
            UserDto customer = customerServiceImpl.getUserDto(username);
            List<Country> countryList = countryServiceImpl.findAll();
            List<City> cities = cityServiceImpl.findAll();
            model.addAttribute("customer", customer);
            model.addAttribute("cities", cities);
            model.addAttribute("countries", countryList);
            model.addAttribute("title", "Profile");
            model.addAttribute("page", "Profile");
            return "profile";
        }
    }

    @PostMapping("/customer/update-profile")
    public String updateProfile(@Valid @ModelAttribute("customer") UserDto customerDto,
                                BindingResult result,
                                RedirectAttributes attributes,
                                Model model,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String username = principal.getName();
            UserDto customer = customerServiceImpl.getUserDto(username);
            List<Country> countryList = countryServiceImpl.findAll();
            List<City> cities = cityServiceImpl.findAll();
            model.addAttribute("countries", countryList);
            model.addAttribute("cities", cities);
            if (result.hasErrors()) {
                return "profile";
            }
            customerServiceImpl.update(customerDto);
            UserDto customerUpdate = customerServiceImpl.getUserDto(principal.getName());
            attributes.addFlashAttribute("success", "Update successfully!");
            model.addAttribute("customer", customerUpdate);
            return "redirect:/customer/profile";
        }
    }

    @GetMapping("/customer/change-password")
    public String changePassword(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Change password");
        model.addAttribute("page", "Change password");
        return "change-password";
    }

    @PostMapping("/customer/change-password")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             @RequestParam("repeatNewPassword") String repeatPassword,
                             RedirectAttributes attributes,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            UserDto customer = customerServiceImpl.getUserDto(principal.getName());
            if (passwordEncoder.matches(oldPassword, customer.getPassword())
                    && !passwordEncoder.matches(newPassword, oldPassword)
                    && !passwordEncoder.matches(newPassword, customer.getPassword())
                    && repeatPassword.equals(newPassword) && newPassword.length() >= 5) {
                customer.setPassword(passwordEncoder.encode(newPassword));
                customerServiceImpl.changePass(customer);
                attributes.addFlashAttribute("success", "Your password has been changed successfully!");
                return "redirect:/customer/profile";
            } else {
                model.addAttribute("message", "Your password is wrong");
                return "change-password";
            }
        }
    }
    
    @GetMapping("/admin/list-customer")
	public String customers(Model model) {
		List<UserDto> customers = customerServiceImpl.findAllbyRole("CUSTOMER");
		model.addAttribute("customers", customers);
		model.addAttribute("size", customers.size());
		List<Country> countryList = countryServiceImpl.findAll();
        List<City> cities = cityServiceImpl.findAll();
        model.addAttribute("countries", countryList);
        model.addAttribute("cities", cities);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-customer";
	}
    
    @GetMapping("/admin/filtered-customers")
    public String getCustomerByCountryAndCity(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "country", required = false) String country, 
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "minQuantity", required = false) Integer minQuantity,
            @RequestParam(name = "maxQuantity", required = false) Integer maxQuantity,Model model) {
    	
    	List<UserDto> customers = customerServiceImpl.getUsersByCityCountryAndRole(city, country, "CUSTOMER", minPrice, maxPrice, minQuantity, maxQuantity);
    	model.addAttribute("customers", customers);
		model.addAttribute("size", customers.size());
		List<Country> countryList = countryServiceImpl.findAll();
        List<City> cities = cityServiceImpl.findAll();
        model.addAttribute("countries", countryList);
        model.addAttribute("cities", cities);
        model.addAttribute("result", "Result:");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}

        return "list-customer";
    }
    
    @GetMapping("/admin/getDetailCustomerById")
    @ResponseBody
    public Object getDetailCustomerById(Long id, Model model) {
    	return customerServiceImpl.getTotalAmountAndProductsByUserId(id);
    }
	
	@GetMapping("/admin/add-customer")
	public String addCustomer(Model model) {
        model.addAttribute("title", "Add Customer");
        model.addAttribute("customerDto", new UserDto());
        List<Country> countryList = countryServiceImpl.findAll();
        List<City> cities = cityServiceImpl.findAll();
        model.addAttribute("countries", countryList);
        model.addAttribute("cities", cities);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "add-customer";
    }

	@PostMapping("admin/save-customer")
	public String saveCustomerString(@ModelAttribute("customerDto") UserDto customerDto, BindingResult result, Model model) {
		try {
			if (result.hasErrors()) {
				model.addAttribute("customerDto", customerDto);
			}
			String usernameString = customerDto.getUsername();
			User customer = customerServiceImpl.findByUsername(usernameString);
			if (customer != null) {
				model.addAttribute("customerDto", customerDto);
				System.out.println("customer not null");
				model.addAttribute("usernameError", "Username has been already!");
				return "add-customer";
			}
			if (customerDto.getPassword().equals(customerDto.getConfirmPassword())) {
				customerDto.setPassword(passwordEncoder.encode(customerDto.getPassword()));
				customerServiceImpl.saveCustomer(customerDto);
				System.out.println("success");
				model.addAttribute("success", "Successfully!");
				model.addAttribute("customerDto", customerDto);
			} else {
				model.addAttribute("customerDto", customerDto);
				model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
				System.out.println("password not same");
				return "add-customer";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errors", "The server has been wrong!");
		}

		return "redirect:/admin/list-customer";
	}

	@GetMapping("/admin/update-customer/{id}")
	public String updateCustomerForm(@PathVariable("id") Long id, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		UserDto customerDto = customerServiceImpl.getById(id);
		model.addAttribute("customerDto", customerDto);
		List<Country> countryList = countryServiceImpl.findAll();
        List<City> cities = cityServiceImpl.findAll();
		model.addAttribute("cities", cities);
        model.addAttribute("countries", countryList);
		return "update-customer";
	}

	@PostMapping("/admin/update-customer")
	public String updateCustomer(@ModelAttribute("customerDto") UserDto customerDto,
			RedirectAttributes redirectAttributes) {
		try {
			customerServiceImpl.update(customerDto);
			redirectAttributes.addFlashAttribute("success", "Update successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
		}
		return "redirect:/admin/list-customer";
	}
	
	@GetMapping(value = "/admin/remove-customer")
	public String removeCustomerString (Long id, RedirectAttributes redirectAttributes) {
		try {
			customerServiceImpl.remove(id);
			redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
		}
		catch (Exception e) {
			e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
		}
		return "redirect:/admin/list-customer";
	}
	
	@RequestMapping(value = "/admin/findCustomerById", method = {RequestMethod.PUT, RequestMethod.GET})
	@ResponseBody
    public Optional<User> findById(Long id, Model model) {
		model.addAttribute("customer", customerServiceImpl.findById(id));
        return customerServiceImpl.findById(id);
    }
}

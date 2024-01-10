package com.onlineshop.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.onlineshop.dto.UserDto;
import com.onlineshop.model.City;
import com.onlineshop.model.Country;
import com.onlineshop.model.User;
import com.onlineshop.service.impl.CityServiceImpl;
import com.onlineshop.service.impl.CountryServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;

@Controller
public class StaffController {
	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private CityServiceImpl cityServiceImpl;
	
	@Autowired
	private CountryServiceImpl countryServiceImpl;

	@GetMapping("/admin/list-staff")
	public String staffs(Model model) {
		List<UserDto> staff = userServiceImpl.findAllbyRole("STAFF");
		model.addAttribute("staffs", staff);
		model.addAttribute("size", staff.size());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-staff";
	}
	
	@GetMapping("/admin/add-staff")
	public String addStaff(Model model) {
        model.addAttribute("title", "Add Staff");
        model.addAttribute("staffDto", new UserDto());
        List<Country> countryList = countryServiceImpl.findAll();
        List<City> cities = cityServiceImpl.findAll();
        model.addAttribute("countries", countryList);
        model.addAttribute("cities", cities);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "add-staff";
    }

	@PostMapping("admin/save-staff")
	public String saveStaffString(@ModelAttribute("staffDto") UserDto staffDto, BindingResult result, Model model) {
		try {
			if (result.hasErrors()) {
				model.addAttribute("staffDto", staffDto);
			}
			String usernameString = staffDto.getUsername();
			User staff = userServiceImpl.findByUsername(usernameString);
			if (staff != null) {
				model.addAttribute("staffDto", staffDto);
				System.out.println("staff not null");
				model.addAttribute("usernameError", "Username has been already!");
				return "add-staff";
			}
			if (staffDto.getPassword().equals(staffDto.getConfirmPassword())) {
				staffDto.setPassword(passwordEncoder.encode(staffDto.getPassword()));
				userServiceImpl.saveStaff(staffDto);
				System.out.println("success");
				model.addAttribute("success", "Successfully!");
				model.addAttribute("staffDto", staffDto);
			} else {
				model.addAttribute("staffDto", staffDto);
				model.addAttribute("passwordError", "Your password maybe wrong! Check again!");
				System.out.println("password not same");
				return "add-staff";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errors", "The server has been wrong!");
		}

		return "redirect:/admin/list-staff";
	}

	@GetMapping("/admin/update-staff/{id}")
	public String updateStaffForm(@PathVariable("id") Long id, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		UserDto staffDto = userServiceImpl.getById(id);
		model.addAttribute("staffDto", staffDto);
		return "update-staff";
	}

	@PostMapping("/admin/update-staff/{id}")
	public String updateStaff(@ModelAttribute("staffDto") UserDto staffDto,
			RedirectAttributes redirectAttributes) {
		try {
			userServiceImpl.update(staffDto);
			redirectAttributes.addFlashAttribute("success", "Update successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
		}
		return "redirect:/admin/list-staff";
	}
	
	@GetMapping(value = "/admin/remove-staff")
	public String removeStaffString (Long id, RedirectAttributes redirectAttributes) {
		try {
			userServiceImpl.remove(id);
			redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
		}
		catch (Exception e) {
			e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
		}
		return "redirect:/admin/list-staff";
	}
	
	@RequestMapping(value = "/admin/findStaffById", method = {RequestMethod.PUT, RequestMethod.GET})
	@ResponseBody
    public Optional<User> findById(Long id, Model model) {
		model.addAttribute("staff", userServiceImpl.findById(id));
        return userServiceImpl.findById(id);
    }

}

package com.onlineshop.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.onlineshop.model.Input;
import com.onlineshop.model.Order;
import com.onlineshop.repository.InputRepository;
import com.onlineshop.service.impl.InputServiceImpl;

@Controller
public class ImportController {
	@Autowired
	private InputServiceImpl inputServiceImpl;
	
	@GetMapping("/admin/list-import")
	public String imports(Model model) {
		List<Input> inputs = inputServiceImpl.findAllInput();
		model.addAttribute("inputs", inputs);
		model.addAttribute("size", inputs.size());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-import";
	}
	
	@GetMapping("/admin/filtered-imports")
	public String filterImport(@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "minTotalPrice", required = false) Double minTotalPrice,
            @RequestParam(name = "maxTotalPrice", required = false) Double maxTotalPrice,
            @RequestParam(name = "minQuantity", required = false) Integer minQuantity,
            @RequestParam(name = "maxQuantity", required = false) Integer maxQuantity, Model model) {
		List<Input> inputs = inputServiceImpl.findInputByFilters(startDate, endDate, minTotalPrice, maxTotalPrice, minQuantity, maxQuantity);
		model.addAttribute("inputs", inputs);
		model.addAttribute("size", inputs.size());
		model.addAttribute("result", "Kết quả: ");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "redirect:/login";
		}
		return "list-import";
	}
}

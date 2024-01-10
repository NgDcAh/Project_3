package com.onlineshop.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlineshop.model.Input;
import com.onlineshop.model.Order;
import com.onlineshop.repository.InputRepository;

@Service
public class InputServiceImpl {
	@Autowired
	private InputRepository inputRepository;
	
	public List<Input> findAllInput() {
		return inputRepository.findAll();
	}
	
	public List<Input> findInputByFilters(LocalDate startDate, LocalDate endDate, Double minTotalPrice, Double maxTotalPrice, Integer minQuantity, Integer maxQuantity) {
		return inputRepository.filterInputs(startDate, endDate, minTotalPrice, maxTotalPrice, minQuantity, maxQuantity);
	}
	
	public List<Integer> getQuantityProductImportByMonthAndYear(){
		List<Integer> quantityProducts = new ArrayList<>();
		Integer year = 2024;
		for(Integer i = 1; i <= 12; i++) {
			Integer quantityProduct = inputRepository.getQuantityProductImport(i, year);
			if(quantityProduct == null) {
				quantityProducts.add(0);
			}
			else {
				quantityProducts.add(quantityProduct);
			}
			
		}
		return quantityProducts;
	}
}

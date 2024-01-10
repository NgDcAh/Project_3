package com.onlineshop.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlineshop.dto.CategoryDto;
import com.onlineshop.model.Category;
import com.onlineshop.repository.CategoryRepository;
import com.onlineshop.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category save(Category category) {
		Category categorySave = new Category(category.getName());
        return categoryRepository.save(categorySave);
	}

	@Override
	public Category update(Category category) {
		Category categoryUpdate = categoryRepository.findById(category.getId()).orElse(null);
        categoryUpdate.setName(category.getName());
        return categoryRepository.save(categoryUpdate);
	}

	@Override
	public List<Category> findAllByActivatedTrue() {
		return categoryRepository.findAllByActivatedTrue();
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Optional<Category> findById(Long id) {
		return categoryRepository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		Category category = categoryRepository.findById(id).orElse(null);
        category.setActivated(false);
        category.setDeleted(true);
        categoryRepository.save(category);
		
	}

	@Override
	public void enableById(Long id) {
		Category category = categoryRepository.findById(id).orElse(null);
        category.setActivated(true);
        category.setDeleted(false);
        categoryRepository.save(category);
	}

	@Override
	public List<CategoryDto> getCategoriesAndSize() {
		List<CategoryDto> categories = categoryRepository.getCategoriesAndSize();
        return categories;
	}
	
	public Map<String, Double> getPercentageSoldByCategory() {
		List<Map<String, Object>> result = categoryRepository.getPercentageSoldByCategory();
		Map<String, Double> resultMap = new HashMap<>();
		for (Map<String, Object> entry : result) {
		    String name = (String) entry.get("name");
		    BigDecimal percentageSoldBigDecimal = (BigDecimal) entry.get("percentage_sold");
            Double percentageSold = percentageSoldBigDecimal.doubleValue();
		    resultMap.put(name, percentageSold);
		}
		return resultMap;
	}
}

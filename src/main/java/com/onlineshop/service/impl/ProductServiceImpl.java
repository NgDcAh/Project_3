package com.onlineshop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.onlineshop.dto.ProductDto;
import com.onlineshop.model.Input;
import com.onlineshop.model.Product;
import com.onlineshop.repository.InputRepository;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.service.ProductService;
import com.onlineshop.utils.ImageUpload;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InputRepository inputRepository;

	@Autowired
	private ImageUpload imageUpload;

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public List<ProductDto> products() {
		return transferData(productRepository.getAllProduct());
	}

	@Override
	public List<ProductDto> allProduct() {
		List<Product> products = productRepository.findAll();
		List<ProductDto> productDtos = transferData(products);
		return productDtos;
	}

	@Override
	public Product save(MultipartFile imageProduct, ProductDto productDto) {
		try {
	        Product product = new Product();
	        Input input = new Input();

	        // Populate Product fields
	        if (imageProduct == null) {
	            product.setImage(null);
	        } else {
	            imageUpload.uploadFile(imageProduct);
	            product.setImage(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
	        }
	        product.setName(productDto.getName());
	        product.setDescription(productDto.getDescription());
	        product.setCurrentQuantity(productDto.getCurrentQuantity());
	        product.setCostPrice(productDto.getCostPrice());
	        product.setSalePrice(productDto.getSalePrice());
	        product.setCategory(productDto.getCategory());
	        product.setBrand(productDto.getBrand());
	        product.set_deleted(false);
	        product.set_activated(true);

	        // Save Product first
	        Product savedProduct = productRepository.save(product);

	        // Populate Input fields and associate with the saved Product
	        input.setProduct(savedProduct);
	        input.setQuantity(productDto.getCurrentQuantity());
	        input.setInputDate(new Date());

	        // Save Input
	        inputRepository.save(input);

	        return savedProduct;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public Product update(MultipartFile imageProduct, ProductDto productDto) {
		try {
			Product productUpdate = productRepository.findById(productDto.getId()).orElse(null);
			if (imageProduct == null) {
				productUpdate.setImage(productUpdate.getImage());
			} else {
				if (imageUpload.checkExist(imageProduct)) {
					productUpdate.setImage(productUpdate.getImage());
				} else {
					imageUpload.uploadFile(imageProduct);
					productUpdate.setImage(Base64.getEncoder().encodeToString(imageProduct.getBytes()));
				}
			}
			productUpdate.setCategory(productDto.getCategory());
			productUpdate.setBrand(productDto.getBrand());
			productUpdate.setId(productUpdate.getId());
			productUpdate.setName(productDto.getName());
			productUpdate.setDescription(productDto.getDescription());
			productUpdate.setCostPrice(productDto.getCostPrice());
			productUpdate.setSalePrice(productDto.getSalePrice());
			productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
			return productRepository.save(productUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	public Input importProduct(ProductDto productDto, int quantity) {
		Input input = new Input();
		Product product = productRepository.findById(productDto.getId()).orElse(null);
		input.setProduct(product);
		input.setQuantity(quantity);
		input.setInputDate(new Date());
		int qtt = product.getCurrentQuantity() + quantity;
		productRepository.updateQuantity(qtt, product.getId());
		return inputRepository.save(input);

	}

	@Override
	public void enableById(Long id) {
		Product product = productRepository.findById(id).orElse(null);
		product.set_activated(true);
		product.set_deleted(false);
		productRepository.save(product);
	}

	@Override
	public void deleteById(Long id) {
		Product product = productRepository.findById(id).orElse(null);
		product.set_deleted(true);
		product.set_activated(false);
		productRepository.save(product);
	}

	@Override
	public ProductDto getById(Long id) {
		ProductDto productDto = new ProductDto();
		Product product = productRepository.findById(id).orElse(null);
		productDto.setId(product.getId());
		productDto.setName(product.getName());
		productDto.setDescription(product.getDescription());
		productDto.setCostPrice(product.getCostPrice());
		productDto.setSalePrice(product.getSalePrice());
		productDto.setCurrentQuantity(product.getCurrentQuantity());
		productDto.setCategory(product.getCategory());
		productDto.setBrand(product.getBrand());
		productDto.setImage(product.getImage());
		return productDto;
	}

	@Override
	public Product findById(Long id) {
		return productRepository.findById(id).get();
	}

	@Override
	public List<ProductDto> randomProduct() {
		return transferData(productRepository.randomProduct());
	}

	@Override
	public Page<ProductDto> searchProducts(int pageNo, String keyword) {
		List<Product> products = productRepository.findAllByNameOrDescription(keyword);
		List<ProductDto> productDtoList = transferData(products);
		Pageable pageable = PageRequest.of(pageNo, 12);
		Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
		return dtoPage;
	}

	@Override
	public Page<ProductDto> getAllProducts(int pageNo) {
		Pageable pageable = PageRequest.of(pageNo, 12);
		List<ProductDto> productDtoLists = this.allProduct();
		Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
		return productDtoPage;
	}

	@Override
	public Page<ProductDto> getAllProductsForCustomer(int pageNo) {
		return null;
	}

	@Override
	public List<ProductDto> findAllByCategory(String category) {
		return transferData(productRepository.findAllByCategory(category));
	}

	@Override
	public Page<ProductDto> filterHighProducts(int pageNo) {
		List<ProductDto> productDtoList = transferData(productRepository.filterHighProducts());
		Pageable pageable = PageRequest.of(pageNo, 12);
		Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
		return dtoPage;
	}

	@Override
	public Page<ProductDto> filterLowerProducts(int pageNo) {
		List<ProductDto> productDtoList = transferData(productRepository.filterLowerProducts());
		Pageable pageable = PageRequest.of(pageNo, 12);
		Page<ProductDto> dtoPage = toPage(productDtoList, pageable);
		return dtoPage;
	}

	@Override
	public List<ProductDto> listViewProducts() {
		return transferData(productRepository.listViewProduct());
	}

	@Override
	public Page<ProductDto> findByCategoryId(int pageNo, Long id) {
		Pageable pageable = PageRequest.of(pageNo, 12);
		List<ProductDto> productDtoLists = transferData(productRepository.getProductByCategoryId(id));
		Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
		return productDtoPage;
	}

	@Override
	public List<ProductDto> searchProducts(String keyword) {
		return transferData(productRepository.searchProducts(keyword));
	}

	private Page toPage(List list, Pageable pageable) {
		if (pageable.getOffset() >= list.size()) {
			return Page.empty();
		}
		int startIndex = (int) pageable.getOffset();
		int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size()) ? list.size()
				: (int) (pageable.getOffset() + pageable.getPageSize());
		List subList = list.subList(startIndex, endIndex);
		return new PageImpl(subList, pageable, list.size());
	}

	private List<ProductDto> transferData(List<Product> products) {
		List<ProductDto> productDtos = new ArrayList<>();
		for (Product product : products) {
			ProductDto productDto = new ProductDto();
			productDto.setId(product.getId());
			productDto.setName(product.getName());
			productDto.setCurrentQuantity(product.getCurrentQuantity());
			productDto.setCostPrice(product.getCostPrice());
			productDto.setSalePrice(product.getSalePrice());
			productDto.setDescription(product.getDescription());
			productDto.setImage(product.getImage());
			productDto.setCategory(product.getCategory());
			productDto.setBrand(product.getBrand());
			productDto.setActivated(product.is_activated());
			productDto.setDeleted(product.is_deleted());
			productDtos.add(productDto);
		}
		return productDtos;
	}

	private Product transfer(ProductDto productDto) {
		Product product = new Product();
		product.setId(productDto.getId());
		product.setName(productDto.getName());
		product.setCurrentQuantity(productDto.getCurrentQuantity());
		product.setCostPrice(productDto.getCostPrice());
		product.setSalePrice(productDto.getSalePrice());
		product.setDescription(productDto.getDescription());
		product.setImage(productDto.getImage());
		product.set_activated(productDto.isActivated());
		product.set_deleted(productDto.isDeleted());
		product.setCategory(productDto.getCategory());
		product.setBrand(productDto.getBrand());
		return product;
	}

	@Override
	public List<ProductDto> randomProductLikeCategory(String category, Long id) {
		return transferData(productRepository.randomProductLikeCategory(category, id));
	}

	public Map<Product, Integer> getTop8Products() {
		List<Map<String, Object>> result = productRepository.getTop8Products();
		Map<Product, Integer> resultMap = new LinkedHashMap<>();
		for (Map<String, Object> entry : result) {
			Product product = productRepository.findById((Long) entry.get("product_id")).orElse(null);
			BigDecimal quantityBigDecimal = (BigDecimal) entry.get("total_quantity_sold");
			Integer quantity = quantityBigDecimal.intValue();
			resultMap.put(product, quantity);
		}
		return resultMap;
	}
	
	public List<ProductDto> getTrendingProduct(){
		List<Map<String, Object>> result = productRepository.getTop8Products();
		List<Product> products = new ArrayList<>();
		for (Map<String, Object> entry : result) {
			Product product = productRepository.findById((Long) entry.get("product_id")).orElse(null);
			products.add(product);
		}
		return transferData(products);
	}

	public List<Integer> getQuantitySoldProductByMonthAndYear() {
		List<Integer> quantityProducts = new ArrayList<>();
		Integer year = 2024;
		for (Integer i = 1; i <= 12; i++) {
			Integer quantity = productRepository.getQuantityProductsSoldByMonthAndYear(i, year);
			if (quantity == null) {
				quantityProducts.add(0);
			} else {
				quantityProducts.add(quantity);
			}

		}
		return quantityProducts;
	}

	public List<ProductDto> getListFilterProduct(String category, String brand, Double minCostPrice, Double maxCostPrice,
			Double minSalePrice, Double maxSalePrice, Integer minCurrentQuantity, Integer maxCurrentQuantity) {
		return transferData(productRepository.filterProducts(category, brand, minCostPrice, maxCostPrice, minSalePrice, maxSalePrice,
				minCurrentQuantity, maxCurrentQuantity));
	}
	
	public Integer getTotalInventory() {
		return productRepository.getTotalInventory();
	}
	
	public List<ProductDto> getProductInRange(Double minSalePrice, Double maxSalePrice){
		return transferData(productRepository.getProductInRage(minSalePrice, maxSalePrice));
	}
	
	
	public Page<ProductDto> getProductsInRangePage(int pageNo, Double minSalePrice, Double maxSalePrice) {
		Pageable pageable = PageRequest.of(pageNo, 12);
		List<ProductDto> productDtoLists = this.getProductInRange(minSalePrice, maxSalePrice) ;
		Page<ProductDto> productDtoPage = toPage(productDtoLists, pageable);
		return productDtoPage;
	}
}

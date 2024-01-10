package com.onlineshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.onlineshop.dto.ProductDto;
import com.onlineshop.model.Product;

public interface ProductService {
	List<Product> findAll();

    List<ProductDto> products();

    List<ProductDto> allProduct();

    Product save(MultipartFile imageProduct, ProductDto product);

    Product update(MultipartFile imageProduct, ProductDto productDto);

    void enableById(Long id);

    void deleteById(Long id);

    ProductDto getById(Long id);

    Product findById(Long id);


    List<ProductDto> randomProduct();
    
    List<ProductDto> randomProductLikeCategory(String category, Long id);

    Page<ProductDto> searchProducts(int pageNo, String keyword);
    

    Page<ProductDto> getAllProducts(int pageNo);

    Page<ProductDto> getAllProductsForCustomer(int pageNo);


    List<ProductDto> findAllByCategory(String category);


    Page<ProductDto> filterHighProducts(int pageNo);

    Page<ProductDto> filterLowerProducts(int pageNo);

    List<ProductDto> listViewProducts();

    Page<ProductDto> findByCategoryId(int pageNo, Long id);

    List<ProductDto> searchProducts(String keyword);
}

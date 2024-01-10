package com.onlineshop.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.onlineshop.dto.CategoryDto;
import com.onlineshop.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	@Query(value = "update Category set name = ?1 where id = ?2")
	Category update(String name, Long id);

	@Query(value = "select * from categories where is_activated = true", nativeQuery = true)
	List<Category> findAllByActivatedTrue();

	@Query(value = "select new com.onlineshop.dto.CategoryDto(c.id, c.name, count(p.category.id)) "
			+ "from Category c left join Product p on c.id = p.category.id "
			+ "where c.activated = true and c.deleted = false " + "group by c.id ")
	List<CategoryDto> getCategoriesAndSize();
	
	@Query(value = "SELECT c.name as name, (COALESCE(SUM(od.quantity), 0) * 100.0 / COALESCE((SELECT SUM(quantity) FROM order_detail), 1)) AS percentage_sold "
			+ "FROM categories c "
			+ "LEFT JOIN products p ON c.category_id = p.category_id "
			+ "LEFT JOIN order_detail od ON p.product_id = od.product_id "
			+ "GROUP BY c.name", nativeQuery = true)
	List<Map<String, Object>> getPercentageSoldByCategory();
	
}

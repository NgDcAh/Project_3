package com.onlineshop.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onlineshop.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	@Query("select p from Product p where p.is_deleted = false and p.is_activated = true")
	List<Product> getAllProduct();

	@Query("select p from Product p where p.name like %?1% or p.description like %?1%")
	List<Product> findAllByNameOrDescription(String keyword);

	@Query("select p from Product p inner join Category c ON c.id = p.category.id"
			+ " where p.category.name = ?1 and p.is_activated = true and p.is_deleted = false")
	List<Product> findAllByCategory(String category);

	@Query(value = "select "
			+ "p.product_id, p.name, p.description, p.current_quantity, p.cost_price, p.category_id, p.brand, p.sale_price, p.image, p.is_activated, p.is_deleted "
			+ "from products p where p.is_activated = true and p.is_deleted = false order by rand() limit 8", nativeQuery = true)
	List<Product> randomProduct();

	@Query(value = "select "
			+ "p.product_id, p.name, p.description, p.current_quantity,p.brand, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted "
			+ "from products p inner join categories c on p.category_id = c.category_id where p.is_activated = true and p.is_deleted = false and c.name = ?1 and p.product_id not like ?2 order by rand() limit 4", nativeQuery = true)
	List<Product> randomProductLikeCategory(String category, Long id);

	@Query(value = "select "
			+ "p.product_id, p.name, p.description, p.current_quantity, p.brand, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted "
			+ "from products p where p.is_deleted = false and p.is_activated = true order by p.cost_price desc ", nativeQuery = true)
	List<Product> filterHighProducts();

	@Query(value = "select "
			+ "p.product_id, p.name, p.description, p.current_quantity, p.brand, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted "
			+ "from products p where p.is_deleted = false and p.is_activated = true order by p.cost_price asc ", nativeQuery = true)
	List<Product> filterLowerProducts();

	@Query(value = "select p.product_id, p.name,p.brand, p.description, p.current_quantity, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted from products p where p.is_deleted = false and p.is_activated = true limit 4", nativeQuery = true)
	List<Product> listViewProduct();

	@Query(value = "select p from Product p inner join Category c on c.id = ?1 and p.category.id = ?1 where p.is_activated = true and p.is_deleted = false")
	List<Product> getProductByCategoryId(Long id);

	@Query("select p from Product p where p.name like %?1% or p.description like %?1%")
	List<Product> searchProducts(String keyword);
	
	@Query(value = "SELECT SUM(current_quantity) AS totalRemainingQuantity "
			+ "FROM products ", nativeQuery = true)
	Integer getTotalInventory();
	
	@Modifying
	@Query(value = "UPDATE `db_onlineshop`.`products` SET `current_quantity` = ?1 WHERE (`product_id` = ?2);", nativeQuery = true)
	void updateQuantity(int quantity, Long productId);
	
	@Query(value = "SELECT p.product_id, SUM(od.quantity) AS total_quantity_sold "+
			"FROM order_detail od "+
			"JOIN products p ON od.product_id = p.product_id "+
			"GROUP BY p.product_id, p.name "+
			"ORDER BY total_quantity_sold DESC "+
			"LIMIT 8;", nativeQuery = true)
	List<Map<String, Object>> getTop8Products();
	
	@Query(value = "SELECT SUM(quantity) AS total_quantity_sold " +
	        "FROM orders " +
	        "WHERE MONTH(order_date) = :month AND YEAR(order_date) = :year", nativeQuery = true)
	Integer getQuantityProductsSoldByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
	
	@Query(value = "SELECT p.* FROM products p " +
	        "JOIN categories c ON p.category_id = c.category_id " +
	        "WHERE (:category_name IS NULL OR c.name LIKE CONCAT('%', :category_name, '%')) "+
            "AND ((:brand IS NULL OR :brand = '') OR p.brand LIKE CONCAT('%', :brand, '%')) "+
	        "AND (COALESCE(:min_cost_price, -1.0) = -1.0 OR p.cost_price >= :min_cost_price) " +
	        "AND (COALESCE(:max_cost_price, 9999999.0) = 9999999.0 OR p.cost_price <= :max_cost_price) " +
	        "AND (COALESCE(:min_sale_price, -1.0) = -1.0 OR p.sale_price >= :min_sale_price) " +
	        "AND (COALESCE(:max_sale_price, 9999999.0) = 9999999.0 OR p.sale_price <= :max_sale_price) " +
	        "AND (COALESCE(:min_quantity, -1) = -1 OR p.current_quantity >= :min_quantity) " +
	        "AND (COALESCE(:max_quantity, 9999999) = 9999999 OR p.current_quantity <= :max_quantity)", nativeQuery = true)
    List<Product> filterProducts(@Param("category_name") String category,
                                 @Param("brand") String brand,
                                 @Param("min_cost_price") Double minCostPrice,
                                 @Param("max_cost_price") Double maxCostPrice,
                                 @Param("min_sale_price") Double minSalePrice,
                                 @Param("max_sale_price") Double maxSalePrice,
                                 @Param("min_quantity") Integer minCurrentQuantity,
                                 @Param("max_quantity") Integer maxCurrentQuantity);
	
	@Query(value = "SELECT p.* FROM products p "+
			"WHERE (COALESCE(:min_sale_price, -1.0) = -1.0 OR p.sale_price >= :min_sale_price) " +
			"AND (COALESCE(:max_sale_price, 9999999.0) = 9999999.0 OR p.sale_price <= :max_sale_price) ", nativeQuery = true)
	List<Product> getProductInRage(@Param("min_sale_price") Double minSalePrice,
                                 @Param("max_sale_price") Double maxSalePrice);


}

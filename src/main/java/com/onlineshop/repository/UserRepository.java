package com.onlineshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.onlineshop.model.User;
import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	@Query(value = "SELECT user.* FROM user " + "INNER JOIN user_role ON user.user_id = user_role.user_id "
			+ "INNER JOIN roles ON user_role.role_id = roles.role_id "
			+ "WHERE roles.name LIKE %?1%", nativeQuery = true)
	List<User> findByRoles(String role);

//	@Query(value = "DELETE ur, u FROM user_role ur " +
//            "INNER JOIN user u ON ur.user_id = u.user_id " +
//            "WHERE ur.user_id = ?1", nativeQuery = true)
//	void removeById(Long id);

	@Modifying
	@Query(value = "DELETE FROM user_role WHERE user_id = ?1", nativeQuery = true)
	void deleteUserRolesByUserId(Long userId);

	@Modifying
	@Query(value = "DELETE FROM user WHERE user_id = ?1", nativeQuery = true)
	void deleteUserById(Long userId);

	@Query(value = "SELECT DISTINCT u.* FROM user u " 
	        + "JOIN cities c ON u.city = c.id "
	        + "JOIN country co ON u.country = co.country_id " 
	        + "JOIN user_role ur ON u.user_id = ur.user_id "
	        + "JOIN roles r ON ur.role_id = r.role_id " 
	        + "JOIN (SELECT user_id, SUM(total_price) AS totalPrice, SUM(quantity) AS total_quantity " +
	               "FROM orders GROUP BY user_id) o ON u.user_id = o.user_id "  // Thêm join với tổng giá trị và tổng số lượng từ bảng orders
	        + "WHERE ((:cityName IS NULL OR :cityName = '') OR c.name LIKE CONCAT('%', :cityName, '%')) "
	        + "AND ((:countryName IS NULL OR :countryName = '') OR co.name LIKE CONCAT('%', :countryName, '%')) "
	        + "AND r.name = :roleName "
	        + "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) "
	        + "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice) "
	        + "AND (:minQuantity IS NULL OR o.total_quantity >= :minQuantity) "
	        + "AND (:maxQuantity IS NULL OR o.total_quantity <= :maxQuantity)", nativeQuery = true)
	List<User> findUsersByCityCountryAndRoleAndOrderDetails(
	        @Param("cityName") String cityName,
	        @Param("countryName") String countryName,
	        @Param("roleName") String roleName,
	        @Param("minPrice") Double minPrice,
	        @Param("maxPrice") Double maxPrice,
	        @Param("minQuantity") Integer minQuantity,
	        @Param("maxQuantity") Integer maxQuantity);

	
	@Query(value = "SELECT user_id, SUM(total_price) AS total_amount_paid, SUM(quantity) AS total_products_purchased, COUNT(order_id) as totalOrder " +
            "FROM orders " +
            "WHERE user_id = :userId " +
            "GROUP BY user_id", nativeQuery = true)
    Object getTotalAmountAndProductsByUserId(@Param("userId") Long userId);
	
	@Query(value = "SELECT user_id, SUM(total_price) AS total_order_price "
			+ "FROM orders "
			+ "GROUP BY user_id "
			+ "ORDER BY total_order_price DESC "
			+ "LIMIT 5;", nativeQuery = true)
	List<Map<String, Object>> getTop5Customer();
}

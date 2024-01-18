package com.onlineshop.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import com.onlineshop.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	@Query(value = "select o.* from orders o where o.order_status like '%Thành công%' and o.is_accept = 1", nativeQuery = true)
	public List<Order> getAllOrders();
	
	@Query(value = "select o.* from orders o where o.order_status like '%Chờ xác nhận%' and o.is_accept = 0", nativeQuery = true)
	public List<Order> getAllOrdersRequest();
	
	@Query(value = "select o.* from orders o where o.order_status like '%Đang vận chuyển%' ", nativeQuery = true)
	public List<Order> getAllOrdersDelivery();
	
	@Query(value = "select o.* from orders o where o.order_status like '%Đã từ chối%' ", nativeQuery = true)
	public List<Order> getAllOrdersReject();
	
	@Query(value = "SELECT COUNT(order_id) AS total_order FROM orders o WHERE o.order_status LIKE '%Chờ xác nhận%' AND o.is_accept = 0", nativeQuery = true)
	public Integer getQuantityOrderRequest();

	@Query(value = "SELECT COUNT(order_id) AS total_order FROM orders o WHERE o.order_status LIKE '%Đang vận chuyển%'", nativeQuery = true)
	public Integer getQuantityOrderDelivery();

	@Query(value = "select sum(input.quantity * products.cost_price) as TotalCost from input join products on input.product_id = products.product_id", nativeQuery = true)
	public Double getInvestment();
	
	@Query(value = "select sum(total_price) as TotalPrice from orders o where o.order_status like '%Thành công%' and o.is_accept = 1", nativeQuery = true)
	public Double getRevenue();
	
	@Query(value = "SELECT COUNT(order_id) AS TotalOrders FROM orders o where o.order_status like '%Thành công%' and o.is_accept = 1", nativeQuery = true)
	public Integer getOrderQuantity();
	
	@Query(value = "SELECT SUM(quantity) AS TotalSoldProducts FROM orders o where o.order_status like '%Thành công%' and o.is_accept = 1", nativeQuery = true)
	public Integer getTotalSoldProducts();
	
	@Query(value = "SELECT SUM(total_price) AS total_revenue "+
			"FROM orders o "+
			"WHERE YEAR(o.order_complete) = ?2 AND MONTH(o.order_complete) = ?1 and o.order_status like '%Thành công%' and o.is_accept = 1 "+
			"GROUP BY YEAR(o.order_complete), MONTH(o.order_complete)", nativeQuery = true)
	public Double getRevenueByMonthAndYear(Integer month, Integer year);
	
	@Query(value = "SELECT SUM(input.quantity * products.cost_price) AS total_cost "+
			"FROM input JOIN products ON input.product_id = products.product_id "+
			"WHERE YEAR(input_date) = ?2 AND MONTH(input_date) = ?1 "+
			"GROUP BY YEAR(input.input_date), MONTH(input.input_date)", nativeQuery = true)
	public Double getInvestmentByMonthAndYear(Integer month, Integer year);
	
	@Modifying
	@Query(value = "DELETE FROM order_detail WHERE order_id = ?1", nativeQuery = true)
	void deleteOrderDetailByOrderId(Long orderId);

	@Modifying
	@Query(value = "DELETE FROM orders WHERE order_id = ?1", nativeQuery = true)
	void deleteOrderById(Long orderId);
	
	@Query(value = "SELECT COUNT(*) AS total_orders " +
	        "FROM orders o " +
	        "WHERE MONTH(o.order_complete) = :month AND YEAR(o.order_complete) = :year and o.order_status like '%Thành công%' and o.is_accept = 1", nativeQuery = true)
	Integer getQuantityOrderByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);
	
	@Query(value = "SELECT o.* FROM orders o " +
	        "WHERE " +
	        "(:startDate IS NULL OR o.order_complete >= :startDate) " +
	        "AND (:endDate IS NULL OR o.order_complete <= :endDate) " +
	        "AND (:minTotalPrice IS NULL OR o.total_price >= :minTotalPrice) " +
	        "AND (:maxTotalPrice IS NULL OR o.total_price <= :maxTotalPrice) " +
	        "AND (:minQuantity IS NULL OR o.quantity >= :minQuantity) " +
	        "AND (:maxQuantity IS NULL OR o.quantity <= :maxQuantity) and o.order_status like '%Thành công%' and o.is_accept = 1", nativeQuery = true)
	List<Order> findOrdersByFilters(
	        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
	        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
	        @Param("minTotalPrice") Double minTotalPrice,
	        @Param("maxTotalPrice") Double maxTotalPrice,
	        @Param("minQuantity") Integer minQuantity,
	        @Param("maxQuantity") Integer maxQuantity
	);
	
	@Query(value = "SELECT o.* FROM orders o " +
	        "WHERE " +
	        "(:startDate IS NULL OR o.order_date >= :startDate) " +
	        "AND (:endDate IS NULL OR o.order_date <= :endDate) " +
	        "AND (:minTotalPrice IS NULL OR o.total_price >= :minTotalPrice) " +
	        "AND (:maxTotalPrice IS NULL OR o.total_price <= :maxTotalPrice) " +
	        "AND (:minQuantity IS NULL OR o.quantity >= :minQuantity) " +
	        "AND (:maxQuantity IS NULL OR o.quantity <= :maxQuantity) and o.order_status like '%Đã từ chối%' ", nativeQuery = true)
	List<Order> findOrdersRejectbyFilter(
	        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
	        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
	        @Param("minTotalPrice") Double minTotalPrice,
	        @Param("maxTotalPrice") Double maxTotalPrice,
	        @Param("minQuantity") Integer minQuantity,
	        @Param("maxQuantity") Integer maxQuantity
	);
	
	@Query(value = "SELECT o.* FROM orders o " +
	        "WHERE " +
	        "(:startDate IS NULL OR o.delivery_date >= :startDate) " +
	        "AND (:endDate IS NULL OR o.delivery_date <= :endDate) " +
	        "AND (:minTotalPrice IS NULL OR o.total_price >= :minTotalPrice) " +
	        "AND (:maxTotalPrice IS NULL OR o.total_price <= :maxTotalPrice) " +
	        "AND (:minQuantity IS NULL OR o.quantity >= :minQuantity) " +
	        "AND (:maxQuantity IS NULL OR o.quantity <= :maxQuantity) and o.order_status like '%Đang vận chuyển%' ", nativeQuery = true)
	List<Order> findOrdersDeliverybyFilter(
	        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
	        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
	        @Param("minTotalPrice") Double minTotalPrice,
	        @Param("maxTotalPrice") Double maxTotalPrice,
	        @Param("minQuantity") Integer minQuantity,
	        @Param("maxQuantity") Integer maxQuantity
	);
	
	@Query(value = "SELECT o.* FROM orders o " +
	        "WHERE " +
	        "(:startDate IS NULL OR o.order_date >= :startDate) " +
	        "AND (:endDate IS NULL OR o.order_date <= :endDate) " +
	        "AND (:minTotalPrice IS NULL OR o.total_price >= :minTotalPrice) " +
	        "AND (:maxTotalPrice IS NULL OR o.total_price <= :maxTotalPrice) " +
	        "AND (:minQuantity IS NULL OR o.quantity >= :minQuantity) " +
	        "AND (:maxQuantity IS NULL OR o.quantity <= :maxQuantity) and o.order_status like '%Chờ xác nhận%' and o.is_accept = 0", nativeQuery = true)
	List<Order> findOrdersRequestbyFilter(
	        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
	        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
	        @Param("minTotalPrice") Double minTotalPrice,
	        @Param("maxTotalPrice") Double maxTotalPrice,
	        @Param("minQuantity") Integer minQuantity,
	        @Param("maxQuantity") Integer maxQuantity
	);


}

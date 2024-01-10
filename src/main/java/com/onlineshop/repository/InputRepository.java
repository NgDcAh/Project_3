package com.onlineshop.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import com.onlineshop.model.Input;


@Repository
public interface InputRepository extends JpaRepository<Input, Long>{

	@Query(value = "SELECT i.* " +
	        "FROM input i " +
	        "JOIN products p ON i.product_id = p.product_id " +
	        "WHERE " +
	        "(COALESCE(:startDate, '0000-01-01') = '0000-01-01' OR i.input_date >= :startDate) " +
	        "AND (COALESCE(:endDate, '9999-12-31') = '9999-12-31' OR i.input_date <= :endDate) " +
	        "AND (COALESCE(:minQuantity, -1) = -1 OR i.quantity >= :minQuantity) " +
	        "AND (COALESCE(:maxQuantity, 9999999) = 9999999 OR i.quantity <= :maxQuantity) " +
	        "AND (COALESCE(:minTotalPrice, -1.0) = -1.0 OR p.cost_price * i.quantity >= :minTotalPrice) " +
	        "AND (COALESCE(:maxTotalPrice, 9999999.0) = 9999999.0 OR p.cost_price * i.quantity <= :maxTotalPrice)", nativeQuery = true)
	List<Input> filterInputs(
	        @Param("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @Param("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	        @Param("minTotalPrice") Double minTotalPrice,
	        @Param("maxTotalPrice") Double maxTotalPrice,
	        @Param("minQuantity") Integer minQuantity,
	        @Param("maxQuantity") Integer maxQuantity
	);
	
	@Query(value = "SELECT SUM(quantity) AS totalQuantity "
			+ "FROM input "
			+ "WHERE MONTH(input_date) = :month AND YEAR(input_date) = :year", nativeQuery = true)
	Integer getQuantityProductImport(@Param("month") Integer month, @Param("year") Integer year);
}

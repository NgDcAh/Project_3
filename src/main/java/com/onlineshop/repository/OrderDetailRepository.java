package com.onlineshop.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.onlineshop.model.Order;
import com.onlineshop.model.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
	@Query("select o from Order o where o.user.id = ?1")
    List<Order> findAllByCustomerId(Long id);
}

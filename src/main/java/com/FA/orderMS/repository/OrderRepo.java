package com.FA.orderMS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.FA.orderMS.entity.Order;

public interface OrderRepo extends CrudRepository<Order, String>{

	public List<Order> findByBuyerId(String buyerId);

	public Optional<Order> findByOrderId(String orderId);

}

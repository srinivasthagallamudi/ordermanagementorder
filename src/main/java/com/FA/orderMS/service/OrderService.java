package com.FA.orderMS.service;

import java.util.List;

import com.FA.orderMS.dto.CartDTO;
import com.FA.orderMS.dto.OrderDTO;
import com.FA.orderMS.dto.PlacedOrderDTO;
import com.FA.orderMS.dto.ProductDTO;
import com.FA.orderMS.exception.OrderException;

public interface OrderService {
	
	public List<OrderDTO> viewAllOrders() throws OrderException;

	public PlacedOrderDTO placeOrder(List<ProductDTO> productList, List<CartDTO> cartList, OrderDTO order) throws OrderException;

	public List<OrderDTO> viewOrdersByBuyer(String buyerId)throws OrderException;

	public OrderDTO viewOrder(String orderId) throws OrderException;

	public String reOrder(String buyerId, String orderId) throws OrderException;


}

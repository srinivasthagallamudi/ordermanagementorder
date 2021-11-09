package com.FA.orderMS.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.FA.orderMS.dto.CartDTO;
import com.FA.orderMS.dto.OrderDTO;
import com.FA.orderMS.dto.PlacedOrderDTO;
import com.FA.orderMS.dto.ProductDTO;
import com.FA.orderMS.entity.Order;
import com.FA.orderMS.entity.OrderedProducts;
import com.FA.orderMS.exception.OrderException;
import com.FA.orderMS.repository.OrderRepo;
import com.FA.orderMS.repository.OrderedProductsRepo;
import com.FA.orderMS.utility.PrimaryKey;
import com.FA.orderMS.utility.Status;
import com.FA.orderMS.validator.ValidateOrder;

@Service(value = "orderService")
@Transactional
public class OrderServiceImpl implements OrderService {
	
	//used to create order id adding after order "O" + index = O1, O2, O3 ...
	private static int index;
	
	@Autowired
	private OrderRepo orderRepo;
	
	@Autowired
	private OrderedProductsRepo prodOrderedRepo;
	
	static {
		index=100;
	}

	
	//method to store fetched orders from entity to dto
	@Override
	public List<OrderDTO> viewAllOrders() throws OrderException {
		Iterable<Order> orders = orderRepo.findAll();
		List<OrderDTO> dtoList = new ArrayList<>();
		orders.forEach(order -> {
			OrderDTO odto = new OrderDTO();
			odto.setOrderId(order.getOrderId());
			odto.setBuyerId(order.getBuyerId());
			odto.setAmount(order.getAmount());
			odto.setAddress(order.getAddress());
			odto.setDate(order.getDate());
			odto.setStatus(order.getStatus());
			dtoList.add(odto);			
		});
		if(dtoList.isEmpty()) throw new OrderException("No orders available");
		return dtoList;
	}

	//method to place order
	@Override
	public PlacedOrderDTO placeOrder(List<ProductDTO> productList, List<CartDTO> cartList, OrderDTO orderDTO) throws OrderException {
		Order order = new Order();
		ValidateOrder.validateOrder(orderDTO);
		String id = "O" + index++;
		order.setOrderId(id);
		order.setAddress(orderDTO.getAddress());
		order.setBuyerId(cartList.get(0).getBuyerId());
		order.setDate(LocalDate.now());
		order.setStatus(Status.ORDER_PLACED.toString());	
		order.setAmount(0f);
		List<OrderedProducts> productsOrdered = new ArrayList<>();
		for(int i = 0; i<cartList.size();i++) {
			ValidateOrder.validateStock(cartList.get(i), productList.get(i));			
			order.setAmount(order.getAmount()+(cartList.get(i).getQuantity()*productList.get(i).getPrice()));
			
			OrderedProducts prodO = new OrderedProducts();
			prodO.setSellerId(productList.get(i).getSellerId());
			prodO.setPrimaryKeys(new PrimaryKey(cartList.get(i).getBuyerId(),productList.get(i).getProdId()));
			prodO.setQuantity(cartList.get(i).getQuantity());
			productsOrdered.add(prodO);				
		}		
		prodOrderedRepo.saveAll(productsOrdered);
		orderRepo.save(order);
		PlacedOrderDTO orderPlaced = new PlacedOrderDTO();
		orderPlaced.setBuyerId(order.getBuyerId());
		orderPlaced.setOrderId(order.getOrderId());
		Integer rewardPts = (int) (order.getAmount()/100);		
		orderPlaced.setRewardPoints(rewardPts);
		
		
		return orderPlaced;
	}

	//method to view order by buyer id
	@Override
	public List<OrderDTO> viewOrdersByBuyer(String buyerId) throws OrderException {
		List<Order> orders = orderRepo.findByBuyerId(buyerId);
		if(orders.isEmpty()) throw new OrderException("No orders available for given BuyerID");
		List<OrderDTO> dtoList = new ArrayList<>();
		orders.forEach(order->{
			OrderDTO odto = new OrderDTO();
			odto.setOrderId(order.getOrderId());
			odto.setBuyerId(order.getBuyerId());
			odto.setAmount(order.getAmount());
			odto.setAddress(order.getAddress());
			odto.setDate(order.getDate());
			odto.setStatus(order.getStatus());
			dtoList.add(odto);
		});
		return dtoList;
	}
	
	//method to view order by order id
	@Override
	public OrderDTO viewOrder(String orderId) throws OrderException {
		Optional<Order> optional = orderRepo.findByOrderId(orderId);
		Order order = optional.orElseThrow(()->new OrderException("Order does not exist"));
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(order.getOrderId());
		orderDTO.setBuyerId(order.getBuyerId());
		orderDTO.setAmount(order.getAmount());
		orderDTO.setAddress(order.getAddress());
		orderDTO.setDate(order.getDate());
		orderDTO.setStatus(order.getStatus());		
		return orderDTO;
	}

	//method to re-order 
	@Override
	public String reOrder(String buyerId, String orderId) throws OrderException {
		Optional<Order> optional = orderRepo.findByOrderId(orderId);
		Order order = optional.orElseThrow(()->new OrderException("Order does not exist for the given buyer"));
		Order reorder = new Order();
		String id = "O" + index++;
		reorder.setOrderId(id);
		reorder.setBuyerId(order.getBuyerId());
		reorder.setAmount(order.getAmount());
		reorder.setAddress(order.getAddress());
		reorder.setDate(LocalDate.now());
		reorder.setStatus(order.getStatus());
		
		orderRepo.save(reorder);		
		return reorder.getOrderId();
	}

}
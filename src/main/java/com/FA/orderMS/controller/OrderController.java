package com.FA.orderMS.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.FA.orderMS.dto.CartDTO;
import com.FA.orderMS.dto.OrderDTO;
import com.FA.orderMS.dto.PlacedOrderDTO;
import com.FA.orderMS.dto.ProductDTO;
import com.FA.orderMS.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@Value("${user.uri}")
	String userUri;
	
	@Value("${product.uri}")
	String productUri;
	
	//placing order by buyer(buyer id)
	@PostMapping(value = "/orderMS/placeOrder/{buyerId}")					
	public ResponseEntity<String> placeOrder(@PathVariable String buyerId, @RequestBody OrderDTO order){
		
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			List<ProductDTO> productList = new ArrayList<>();
			List<CartDTO> cartList = mapper.convertValue(
					new RestTemplate().getForObject(userUri+"/userMS/buyer/cart/get/" + buyerId, List.class), 
				    new TypeReference<List<CartDTO>>(){}
				);
			
			cartList.forEach(item ->{
				ProductDTO prod = new RestTemplate().getForObject(productUri+"/prodMS/getById/" +item.getProdId(),ProductDTO.class) ; //getByProdId/{productId}
				System.out.println(prod.getDescription());
				productList.add(prod);
			});
			
			PlacedOrderDTO orderPlaced = orderService.placeOrder(productList,cartList,order);
			cartList.forEach(item->{
				new RestTemplate().getForObject(productUri+"/prodMS/updateStock/" +item.getProdId()+"/"+item.getQuantity(), boolean.class) ;
				new RestTemplate().postForObject(userUri+"/userMS/buyer/cart/remove/"+buyerId+"/"+item.getProdId(),null, String.class);
			});			
			
			new RestTemplate().getForObject(userUri+"/userMS/updateRewardPoints/"+buyerId+"/"+orderPlaced.getRewardPoints() , String.class);
			
			return new ResponseEntity<>(orderPlaced.getOrderId(),HttpStatus.ACCEPTED);
		}
		catch(Exception e)
		{
			String newMsg = "There was some error";
			if(e.getMessage().equals("404 null"))
			{
				newMsg = "Error while placing the order";
			}
			return new ResponseEntity<>(newMsg,HttpStatus.UNAUTHORIZED);
		}		
		
	}
	
	//view all orders
	@GetMapping(value = "/orderMS/viewAll")						
	public ResponseEntity<List<OrderDTO>> viewAllOrder(){		
		try {
			List<OrderDTO> allOrders = orderService.viewAllOrders();
			return new ResponseEntity<>(allOrders,HttpStatus.OK);
		}
		catch(Exception e)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
			
		}		
	}
	
	//view orders of specfic buyer(buyer id)
	@GetMapping(value = "/orderMS/viewOrders/{buyerId}")					
	public ResponseEntity<List<OrderDTO>> viewsOrdersByBuyerId(@PathVariable String buyerId){		
		try {
			List<OrderDTO> allOrders = orderService.viewOrdersByBuyer(buyerId);
			return new ResponseEntity<>(allOrders,HttpStatus.OK);
		}
		catch(Exception e)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}		
	}
	
	//view orders by order id
	@GetMapping(value = "/orderMS/viewOrder/{orderId}")							
	public ResponseEntity<OrderDTO> viewsOrderByOrderId(@PathVariable String orderId){		
		try {
			OrderDTO allOrders = orderService.viewOrder(orderId);
			return new ResponseEntity<>(allOrders,HttpStatus.OK);
		}
		catch(Exception e)
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}		
	}
	
	//to re-order an order(order id) by buyer(buyer id)
	@PostMapping(value = "/orderMS/reOrder/{buyerId}/{orderId}")							
	public ResponseEntity<String> reOrder(@PathVariable String buyerId, @PathVariable String orderId){
		
		try {
			
			String id = orderService.reOrder(buyerId,orderId);
			return new ResponseEntity<>("Order ID: "+id,HttpStatus.ACCEPTED);
		}
		catch(Exception e)
		{
			return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
		}		
	}
	
	//to add product(product id) in cart of buyer(buyer id)
	@PostMapping(value = "/orderMS/addToCart/{buyerId}/{prodId}/{quantity}")						
	public ResponseEntity<String> addToCart(@PathVariable String buyerId, @PathVariable String prodId,@PathVariable Integer quantity){
		
		try {
			
			
			String successMsg = new RestTemplate().postForObject(userUri+"/userMS/buyer/cart/add/"+buyerId+"/"+prodId+"/"+quantity, null, String.class);

			return new ResponseEntity<>(successMsg,HttpStatus.ACCEPTED);
		}
		catch(Exception e)
		{
			String newMsg = "There was some error";
			if(e.getMessage().equals("404 null"))
			{
				newMsg = "There are no PRODUCTS for the given product ID";
			}
			return new ResponseEntity<>(newMsg,HttpStatus.UNAUTHORIZED);
		}		
	}
	
	//to remove product(product id) from cart of buyer(buyer id)
	@PostMapping(value = "/orderMS/removeFromCart/{buyerId}/{prodId}")						
	public ResponseEntity<String> removeFromCart(@PathVariable String buyerId, @PathVariable String prodId){
		
		try {
			
			String successMsg = new RestTemplate().postForObject(userUri+"/userMS/buyer/cart/remove/"+buyerId+"/"+prodId, null, String.class);
			
			return new ResponseEntity<>(successMsg,HttpStatus.ACCEPTED);
		}
		catch(Exception e)
		{
			String newMsg = "There was some error";
			if(e.getMessage().equals("404 null"))
			{
				newMsg = "There are no PRODUCTS for the given product ID";
			}
			return new ResponseEntity<>(newMsg,HttpStatus.UNAUTHORIZED);
		}		
	}
	
	

}

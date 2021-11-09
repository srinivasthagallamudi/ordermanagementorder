package com.FA.orderMS.validator;

import com.FA.orderMS.dto.CartDTO;
import com.FA.orderMS.dto.OrderDTO;
import com.FA.orderMS.dto.ProductDTO;
import com.FA.orderMS.exception.OrderException;

public class ValidateOrder {
	
	public static void validateOrder(OrderDTO order) throws OrderException {
		
		//Address must be within 100 characters
		if(!validateAddress(order.getAddress()))
			throw new OrderException("Invalid number of address characters.");		
		
	}
	
	public static void validateStock(CartDTO cart, ProductDTO product) throws OrderException {
				
		//Check if the required quantity of product is available in the stock
		if(!validateStock(product.getStock(),cart.getQuantity()))
			throw new OrderException("Insufficient stock");	
	}
	
	
	private static boolean validateAddress(String address) {		
		return (address.length()>0 &&address.length()<100);		
	}
	
	private static boolean validateStock(Integer stock, Integer quantity) {		
		return stock>=quantity;		
	}
}

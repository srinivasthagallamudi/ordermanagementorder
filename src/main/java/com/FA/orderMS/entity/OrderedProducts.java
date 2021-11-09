package com.FA.orderMS.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.FA.orderMS.utility.PrimaryKey;

@Entity
@Table(name = "products_ordered")
public class OrderedProducts {
	
	@EmbeddedId
	private PrimaryKey primaryKeys;
	
	private String sellerId;	
	private Integer quantity;
	public PrimaryKey getPrimaryKeys() {
		return primaryKeys;
	}
	public void setPrimaryKeys(PrimaryKey primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}
package com.FA.orderMS.repository;

import org.springframework.data.repository.CrudRepository;

import com.FA.orderMS.entity.OrderedProducts;
import com.FA.orderMS.utility.PrimaryKey;

public interface OrderedProductsRepo extends CrudRepository<OrderedProducts, PrimaryKey>{

}


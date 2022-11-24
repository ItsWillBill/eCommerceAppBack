package com.luv2code.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.luv2code.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}

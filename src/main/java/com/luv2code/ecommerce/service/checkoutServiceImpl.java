package com.luv2code.ecommerce.service;

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luv2code.ecommerce.dao.CustomerRepository;
import com.luv2code.ecommerce.dto.Purchase;
import com.luv2code.ecommerce.dto.PurchaseResponse;
import com.luv2code.ecommerce.entity.Customer;
import com.luv2code.ecommerce.entity.Order;
import com.luv2code.ecommerce.entity.OrderItem;

@Service
public class checkoutServiceImpl implements CheckoutService {
	
	@Autowired
	CustomerRepository customerRepo;
	
	@Override
	@Transactional
	public PurchaseResponse placeOrder(Purchase purchase) {
		
		// retrieve the order info from dto
		Order order = purchase.getOrder();
		
		// generate tracking number
		String orderTrackingNumber = generateOrderTrackingNumber();
		order.setOrderTrackingNumber(orderTrackingNumber);
		
		// populate order with orderItems
		Set<OrderItem> orderItems = purchase.getOrderItems();
		orderItems.forEach(item -> order.add(item));
		
		// populate order with billingAddress and shippingAddress
		order.setBillingAddress(purchase.getBillingAddress());
		order.setShippingAddress(purchase.getShippingAddress());
		
		// populate customer with order 
		Customer customer = purchase.getCustomer();
		
		// check if this is an existing customer
		
		String theEmail = customer.getEmail();
		
		Customer customerFromDB = customerRepo.findByEmail(theEmail);
		
		if (customerFromDB != null) {
			// we found them ... let's assign them accordingly
			customer = customerFromDB;
		}
		
		customer.add(order);
		
		// save to the db
		customerRepo.save(customer);
		
		// return the response
		return new PurchaseResponse(orderTrackingNumber);
	}

	private String generateOrderTrackingNumber() {
		
		// generate a random UUID number (UUID v4)
		
		return UUID.randomUUID().toString();
	}
	
	
}

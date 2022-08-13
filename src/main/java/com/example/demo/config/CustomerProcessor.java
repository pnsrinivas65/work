package com.example.demo.config;

import org.springframework.batch.item.ItemProcessor;

import com.example.demo.entity.CustomerInfo;

public class CustomerProcessor implements ItemProcessor<CustomerInfo, CustomerInfo>{

	@Override
	public CustomerInfo process(CustomerInfo item) throws Exception {
		return item;
	}
	

}

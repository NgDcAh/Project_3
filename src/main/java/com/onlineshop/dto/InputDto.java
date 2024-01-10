package com.onlineshop.dto;

import java.sql.Date;

import com.onlineshop.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputDto {
	private Long id;
	private Product product;
	private int quantity;
	private Date inputDate;
}

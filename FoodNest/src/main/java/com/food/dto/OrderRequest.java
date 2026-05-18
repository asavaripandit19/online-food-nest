package com.food.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderRequest {

   
    
    

    private UUID customerId;

    private BigDecimal totalAmount;
}

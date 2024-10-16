package com.example.nagoyamesi.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionForm {
	private String cardHolderName; 
    private String cardNumber;     
    private String expiryYear;     
    private String expiryMonth;    
    private String cvc;            
    private String postalCode;     
}
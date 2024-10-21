package com.example.nagoyamesi.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "subscription")
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "stripe_customer_id", nullable = false)
    private String stripeCustomerId;
    
    @Column(name = "stripe_subscription_id", nullable = false)
    private String stripeSubscriptionId;
    
    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;
    
    @Column(name = "end_date")
    private Timestamp endDate;
}
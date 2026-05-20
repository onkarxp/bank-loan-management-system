package com.BankLoanManagement.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Tells Spring Boot and Hibernate that this Java class represents a database table.
@Table(name="Customer")
@Data // Lombok annotation which generates all boilerplate code (getters, setters, toString) at compile time
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //auto: auto might generate same value, so whole concept of unique id gone
    private Integer customerId; // Primary key, auto-incremented

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String phone;

    // TEXT in SQL usually maps to a standard String in Java. We use length to hint to Hibernate.
    @Column(nullable = false, length = 500) 
    private String address;

    // Defines a custom enumeration type, restricting the values this variable can hold.
    public enum KycStatus {
        PENDING, VERIFIED
    }

    // Tells hibernate to store enum text (e.g., "PENDING") instead of a numerical index
    
    //it should display string hence writing this
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus kycStatus;

}
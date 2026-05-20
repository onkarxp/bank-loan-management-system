package com.BankLoanManagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BankLoanManagement.entities.Customer;
import com.BankLoanManagement.services.CustomerService;
 

@RestController // Tells spring that this class receives the web requests and automatically converts the returned java data into json
@RequestMapping("/customers") // Sets the base URL path. Every endpoint in this file will automatically start with "http://localhost:8091/customers".
public class CustomerController {
 
 
    private CustomerService customerService;
 
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
 
    // STARTING TO WRITE ENDPOINTS
    
    // 1. Register a new customer
    @PostMapping
    public ResponseEntity<Customer> registerCustomer(@RequestBody Customer customer) {
            return new ResponseEntity<>(customerService.registerCustomer(customer), HttpStatus.CREATED);
    }
 
    // 2. Fetch customer details based on ID
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerDetails(@PathVariable Integer customerId) {
     	return new ResponseEntity<>(customerService.getCustomerDetails(customerId), HttpStatus.OK);
    }
    // 3. GetAllcustomer
    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<Customer>> getAllCustomers(){
    	 List<Customer> l = customerService.getAllCustomers() ;
    	 return new ResponseEntity<>(l , HttpStatus.OK) ;
    }
 
    // 4. Update customer profile details
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomerProfile(@PathVariable Integer customerId, @RequestBody Customer updatedDetails) {
            return new ResponseEntity<>(customerService.updateCustomerProfile(customerId, updatedDetails), HttpStatus.OK);
    }
 
    // 5. Fetch the current KYC verification status
    @GetMapping("/{customerId}/kyc-status")
    public ResponseEntity<Customer.KycStatus> getKycStatus(@PathVariable Integer customerId) {
            return new ResponseEntity<>(customerService.getKycStatus(customerId), HttpStatus.OK);
    }
 
    // 6. Verify or update the customer's KYC status 4/kyc-status?status=PENDING
    @PutMapping("/{customerId}/kyc-status")
    public ResponseEntity<Customer> updateKycStatus(
            @PathVariable Integer customerId,
            @RequestParam Customer.KycStatus status) {
            return new ResponseEntity<>(customerService.updateKycStatus(customerId, status), HttpStatus.OK);
 
    }
    
    
    
    
//    @GetMapping("/email/{email}")
//    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email){
//    	       return new ResponseEntity<>(customerService.getCustomerByEmail(email),HttpStatus.OK);
//    }
    
    
//    @GetMapping("/kyc-status/{status}")
//    public ResponseEntity<List<Customer>> getCustomerByKycStatus(@PathVariable Customer.KycStatus status){
//    	  return new ResponseEntity<>(customerService.getCustomerByKycStatus(status),HttpStatus.OK);
//    }
}
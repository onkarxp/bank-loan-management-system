package com.BankLoanManagement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.BankLoanManagement.entities.LoanApplication;
import com.BankLoanManagement.entities.Repayments;
import com.BankLoanManagement.exceptions.ResourceNotFoundException;
import com.BankLoanManagement.repositories.LoanApplicationRepo;
import com.BankLoanManagement.repositories.RepaymentsRepo;
import com.BankLoanManagement.services.RepaymentsService;

@ExtendWith(MockitoExtension.class)
public class RepaymentsServiceTest {

    // ==========================================
    // MOCKITO SETUP
    // ==========================================

    @Mock
    private RepaymentsRepo repaymentRepo;

    @Mock
    private LoanApplicationRepo loanApplicationRepo;

    // Injects the mocked repos into the real service
    @InjectMocks
    private RepaymentsService repaymentsService;

    // Global test objects
    private Repayments testEmi;
    private LoanApplication testApp;

    // Runs before EVERY test to give us clean data
    @BeforeEach
    void setUp() {
        testApp = new LoanApplication();
        testApp.setApplicationId(101);

        testEmi = new Repayments();
        testEmi.setRepaymentId(1);
        testEmi.setLoanApplication(testApp);
        testEmi.setAmountDue(new BigDecimal("5000.00"));
        testEmi.setDueDate(LocalDate.now().plusMonths(1)); // Safe future due date
        testEmi.setPaymentStatus(Repayments.PaymentStatus.PENDING);
    }

    // ==========================================
    // TESTS FOR: processPayment()
    // ==========================================

    @Test
    void testProcessPayment_Success() throws ResourceNotFoundException {
        // 1. ARRANGE
        // When the service asks for EMI #1, give it our test object
        when(repaymentRepo.findById(1)).thenReturn(Optional.of(testEmi));
        // Mock the security check: say there are 0 unpaid older EMIs
        when(repaymentRepo.countByLoanApplication_ApplicationIdAndPaymentStatusAndDueDateBefore(
                101, Repayments.PaymentStatus.PENDING, testEmi.getDueDate())).thenReturn(0);
        // Mock the save action to just return the object
        when(repaymentRepo.save(any(Repayments.class))).thenReturn(testEmi);

        // 2. ACT
        Repayments result = repaymentsService.processPayment(1);

        // 3. ASSERT
        assertNotNull(result);
        assertEquals(Repayments.PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertNotNull(result.getPaymentDate());
        verify(repaymentRepo, times(1)).save(testEmi); // Ensure save() was actually called
    }

    @Test
    void testProcessPayment_ThrowsException_WhenAlreadyPaid() {
        // 1. ARRANGE
        // Corrupt the data: make it already paid
        testEmi.setPaymentStatus(Repayments.PaymentStatus.COMPLETED);
        when(repaymentRepo.findById(1)).thenReturn(Optional.of(testEmi));

        // 2. ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            repaymentsService.processPayment(1);
        });
        
        assertEquals("The EMI has been already paid", exception.getMessage());
        // Verify save() was NEVER called because the exception blocked it
        verify(repaymentRepo, never()).save(any(Repayments.class)); 
    }

    @Test
    void testProcessPayment_ThrowsException_WhenUnpaidOlderEmisExist() {
        // 1. ARRANGE
        when(repaymentRepo.findById(1)).thenReturn(Optional.of(testEmi));
        // Trigger the security check: say there are 2 unpaid older EMIs!
        when(repaymentRepo.countByLoanApplication_ApplicationIdAndPaymentStatusAndDueDateBefore(
                101, Repayments.PaymentStatus.PENDING, testEmi.getDueDate())).thenReturn(2); 

        // 2. ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            repaymentsService.processPayment(1);
        });
        
        assertTrue(exception.getMessage().contains("Payment Blocked"));
    }

    // ==========================================
    // TESTS FOR: calculateOutStandingBalance()
    // ==========================================

    @Test
    void testCalculateOutStandingBalance_AppliesPenaltyCorrectly() throws ResourceNotFoundException {
        // 1. ARRANGE
        // Create an EMI that is exactly 1 month overdue
        Repayments overdueEmi = new Repayments();
        overdueEmi.setAmountDue(new BigDecimal("5000.00"));
        // Setting date to 1 month and 1 day ago to trigger ChronoUnit logic cleanly
        overdueEmi.setDueDate(LocalDate.now().minusMonths(1).minusDays(1)); 
        overdueEmi.setPaymentStatus(Repayments.PaymentStatus.PENDING);

        List<Repayments> pendingEmis = new ArrayList<>();
        pendingEmis.add(overdueEmi);

        // Feed the mock data to the database call
        when(repaymentRepo.findByLoanApplication_ApplicationIdAndPaymentStatus(
                101, Repayments.PaymentStatus.PENDING)).thenReturn(pendingEmis);

        // 2. ACT
        BigDecimal balance = repaymentsService.calculateOutStandingBalance(101);

        // 3. ASSERT
        // Math breakdown: 1 month passed -> Penalty count = 1 + 1 = 2.
        // 2 * 850 = 1700 penalty. Total expected = 5000 + 1700 = 6700.00
        assertEquals(new BigDecimal("6700.00"), balance);
    }

    // ==========================================
    // TESTS FOR: getRepaymentSchedule()
    // ==========================================

    @Test
    void testGetRepaymentSchedule_ThrowsException_WhenScheduleEmpty() {
        // 1. ARRANGE
        // Mock a bad application ID that returns an empty list
        when(repaymentRepo.findByLoanApplication_ApplicationId(999)).thenReturn(new ArrayList<>());

        // 2. ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            repaymentsService.getRepaymentSchedule(999);
        });
        
        assertTrue(exception.getMessage().contains("No repayment schedule found"));
    }

    // ==========================================
    // TESTS FOR: manualStatusOverride()
    // ==========================================

    @Test
    void testManualStatusOverride_Success_ToCompleted() throws ResourceNotFoundException {
        // 1. ARRANGE
        when(repaymentRepo.findById(1)).thenReturn(Optional.of(testEmi));
        when(repaymentRepo.save(any(Repayments.class))).thenReturn(testEmi);

        // 2. ACT
        Repayments result = repaymentsService.manualStatusOverride(1, Repayments.PaymentStatus.COMPLETED);

        // 3. ASSERT
        assertEquals(Repayments.PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertNotNull(result.getPaymentDate()); // God mode should log the date
    }
}
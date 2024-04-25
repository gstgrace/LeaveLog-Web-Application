package com.lms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lms.repository.EmployeeRepository;
import com.lms.repository.LeaveManageNativeSqlRepo;
import com.lms.repository.LeaveTypeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.lms.models.LeaveDetails;
import com.lms.models.LeaveType;
import com.lms.models.Manager;
import com.lms.models.NonManagerEmployee;
import com.lms.models.UserLeaveBalance;
import com.lms.repository.LeaveManageRepository;
import com.lms.repository.UserLeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class LeaveManageServiceTest {

  private LeaveManageService leaveManageService;
  private LeaveManageRepository leaveManageRepository;
  private UserLeaveBalanceRepository userLeaveBalanceRepository;

  @BeforeEach
  void setUp() {
    leaveManageRepository = mock(LeaveManageRepository.class);
    userLeaveBalanceRepository = mock(UserLeaveBalanceRepository.class);
    leaveManageService = new LeaveManageService(
        leaveManageRepository,
        mock(LeaveManageNativeSqlRepo.class),
        userLeaveBalanceRepository,
        mock(EmployeeRepository.class),
        mock(LeaveTypeRepository.class)
    );
  }

  @Test
  void testApplyLeave() {
    // Given
    LeaveDetails leaveDetails = new LeaveDetails();
    leaveDetails.setUser(new NonManagerEmployee()); // Assuming NonManagerEmployee extends AbstractEmployee
    leaveDetails.setLeaveType(new LeaveType());
    leaveDetails.setFromDate(LocalDate.now());
    leaveDetails.setToDate(LocalDate.now().plusDays(2));
    UserLeaveBalance userLeaveBalance = new UserLeaveBalance();
    userLeaveBalance.setCurrentBalance(5); // Sufficient balance
    when(userLeaveBalanceRepository.findByUser_IdAndLeaveType_IdAndYear(anyInt(), anyInt(), anyInt()))
        .thenReturn(Optional.of(userLeaveBalance));

    // When
    leaveManageService.applyLeave(leaveDetails);

    // Then
    ArgumentCaptor<LeaveDetails> leaveDetailsCaptor = ArgumentCaptor.forClass(LeaveDetails.class);
    verify(leaveManageRepository).save(leaveDetailsCaptor.capture());
    LeaveDetails savedLeaveDetails = leaveDetailsCaptor.getValue();
    assertEquals("PENDING", savedLeaveDetails.getStatus());
    assertNotNull(savedLeaveDetails.getDuration());
  }

  @Test
  void testApproveLeave() {
    // Given
    LeaveDetails leaveDetails = new LeaveDetails();
    leaveDetails.setId(1);
    leaveDetails.setUser(new Manager()); // Assuming Manager extends AbstractEmployee
    leaveDetails.setLeaveType(new LeaveType());
    leaveDetails.setDuration(2);
    UserLeaveBalance userLeaveBalance = new UserLeaveBalance();
    userLeaveBalance.setCurrentBalance(5); // Sufficient balance
    when(userLeaveBalanceRepository.findByUser_IdAndLeaveType_IdAndYear(anyInt(), anyInt(), anyInt()))
        .thenReturn(Optional.of(userLeaveBalance));
    when(leaveManageRepository.findById(anyInt())).thenReturn(Optional.of(leaveDetails));

    // When
    leaveManageService.approveLeave(leaveDetails.getId());

    // Then
    ArgumentCaptor<LeaveDetails> leaveDetailsCaptor = ArgumentCaptor.forClass(LeaveDetails.class);
    verify(leaveManageRepository, times(2)).save(leaveDetailsCaptor.capture());
    List<LeaveDetails> capturedLeaveDetails = leaveDetailsCaptor.getAllValues();
    assertEquals("APPROVED", capturedLeaveDetails.get(0).getStatus());
    assertEquals("APPROVED", capturedLeaveDetails.get(1).getStatus());

    // Verify that userLeaveBalanceRepository.save is called
    verify(userLeaveBalanceRepository).save(any(UserLeaveBalance.class));
  }



  @Test
  void testRejectLeave() {
    // Given
    int leaveId = 1;
    LeaveDetails leaveDetails = new LeaveDetails();
    leaveDetails.setId(leaveId);

    // Mock the findById method to return a non-empty Optional with the leaveDetails object
    when(leaveManageRepository.findById(leaveId)).thenReturn(Optional.of(leaveDetails));

    // When
    leaveManageService.rejectLeave(leaveId);

    // Then
    ArgumentCaptor<LeaveDetails> leaveDetailsCaptor = ArgumentCaptor.forClass(LeaveDetails.class);
    verify(leaveManageRepository).save(leaveDetailsCaptor.capture());
    LeaveDetails savedLeaveDetails = leaveDetailsCaptor.getValue();
    assertEquals("REJECTED", savedLeaveDetails.getStatus());
  }

  // Add more tests as needed
}

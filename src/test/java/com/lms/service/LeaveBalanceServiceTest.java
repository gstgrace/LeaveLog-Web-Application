package com.lms.service;

import static org.mockito.Mockito.*;

import com.lms.models.NonManagerEmployee;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lms.models.AbstractEmployee;
import com.lms.models.LeaveType;
import com.lms.models.UserLeaveBalance;
import com.lms.repository.LeaveTypeRepository;
import com.lms.repository.UserLeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

class LeaveBalanceServiceTest {

  private LeaveBalanceService leaveBalanceService;
  private LeaveTypeRepository leaveTypeRepository;
  private UserLeaveBalanceRepository userLeaveBalanceRepository;

  @BeforeEach
  void setUp() {
    leaveTypeRepository = mock(LeaveTypeRepository.class);
    userLeaveBalanceRepository = mock(UserLeaveBalanceRepository.class);
    leaveBalanceService = new LeaveBalanceService(leaveTypeRepository, userLeaveBalanceRepository);
  }

  @Test
  void testUpdateAnnualLeaveBalances() {
    LeaveType leaveType = new LeaveType();
    leaveType.setTypeName("Vacation Leave");
    leaveType.setAnnualAllocation(10);
    leaveType.setMaxAccumulation(20);

    UserLeaveBalance userLeaveBalance = new UserLeaveBalance();
    userLeaveBalance.setLeaveType(leaveType);
    userLeaveBalance.setCurrentBalance(15);

    List<UserLeaveBalance> balances = new ArrayList<>();
    balances.add(userLeaveBalance);

    when(userLeaveBalanceRepository.findAll()).thenReturn(balances);

    leaveBalanceService.updateAnnualLeaveBalances();
    verify(userLeaveBalanceRepository, times(1)).save(userLeaveBalance);
  }

  @Test
  void testInitializeLeaveBalancesForNewUser() {
    // Given
    NonManagerEmployee employee = new NonManagerEmployee();
    employee.setOnboardingDate(LocalDate.of(2024, 1, 1));

    LeaveType leaveType = new LeaveType();
    leaveType.setTypeName("Vacation Leave");
    leaveType.setAnnualAllocation(20);

    List<LeaveType> leaveTypes = new ArrayList<>();
    leaveTypes.add(leaveType);

    when(leaveTypeRepository.findAll()).thenReturn(leaveTypes);

    leaveBalanceService.initializeLeaveBalancesForNewUser(employee);
    verify(userLeaveBalanceRepository, times(1)).save(any(UserLeaveBalance.class));
  }
}

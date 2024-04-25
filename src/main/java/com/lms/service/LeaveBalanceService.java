package com.lms.service;

import com.lms.models.AbstractEmployee;
import com.lms.models.LeaveType;
import com.lms.models.UserLeaveBalance;
import com.lms.repository.LeaveTypeRepository;
import com.lms.repository.UserLeaveBalanceRepository;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing leave balances. This includes scheduled tasks for updating leave balances
 * annually and initializing leave balances for new employees.
 */
@Service
public class LeaveBalanceService {

  private final LeaveTypeRepository leaveTypeRepository;
  private final UserLeaveBalanceRepository userLeaveBalanceRepository;

  public LeaveBalanceService(LeaveTypeRepository leaveTypeRepository,
      UserLeaveBalanceRepository userLeaveBalanceRepository) {
    this.leaveTypeRepository = leaveTypeRepository;
    this.userLeaveBalanceRepository = userLeaveBalanceRepository;
  }

  /**
   * Scheduled task to update annual leave balances for all users at the start of each year.
   * This method calculates new balances based on the previous balance and the annual allocation,
   * respecting the maximum accumulation rules.
   */
  @Scheduled(cron = "0 0 0 1 1 *") // Runs at midnight on January 1st of every year
  @Transactional
  public void updateAnnualLeaveBalances() {
    List<UserLeaveBalance> balances = userLeaveBalanceRepository.findAll();
    for (UserLeaveBalance balance : balances) {
      LeaveType type = balance.getLeaveType();
      if (type.getAnnualAllocation() != null && type.getMaxAccumulation() != null) {
        int newBalance = balance.getCurrentBalance() + type.getAnnualAllocation();
        newBalance = Math.min(type.getMaxAccumulation(), newBalance);
        balance.setCurrentBalance(newBalance);
        userLeaveBalanceRepository.save(balance);
      }
    }
  }

  /**
   * Initializes leave balances for a new user based on their onboarding date and applicable leave types.
   * Balances are prorated if the user is onboarded part-way through the year.
   *
   * @param employee the employee for whom to initialize leave balances
   */
  public void initializeLeaveBalancesForNewUser(AbstractEmployee employee) {
    List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
    int currentYear = Year.now().getValue();
    LocalDate onboardingDate = employee.getOnboardingDate();
    int quarter = (onboardingDate.getMonthValue() - 1) / 3 + 1;  // Calculate the quarter of the onboarding date

    leaveTypes.forEach(leaveType -> {
      if (leaveType.getAnnualAllocation() != null) {
        double proRatedBalance = calculateProratedBalance(leaveType, quarter);
        UserLeaveBalance balance = new UserLeaveBalance();
        balance.setUser(employee);
        balance.setLeaveType(leaveType);
        balance.setCurrentBalance((int) Math.ceil(proRatedBalance));
        balance.setYear(currentYear);
        userLeaveBalanceRepository.save(balance);
      }
    });
  }

  /**
   * Helper method to calculate prorated leave balance based on the quarter of the year.
   *
   * @param leaveType the type of leave being calculated
   * @param quarter the quarter in which the employee was onboarded
   * @return the prorated balance based on the quarter
   */
  private double calculateProratedBalance(LeaveType leaveType, int quarter) {
    if ("Vacation Leave".equals(leaveType.getTypeName())) {
      switch (quarter) {
        case 1: return leaveType.getAnnualAllocation() * 0.75;
        case 2: return leaveType.getAnnualAllocation() * 0.5;
        case 3: return leaveType.getAnnualAllocation() * 0.25;
        case 4: return leaveType.getAnnualAllocation() * 0.0;
        default: return 0.0;
      }
    }
    return leaveType.getAnnualAllocation(); // Full allocation for other leave types
  }
}

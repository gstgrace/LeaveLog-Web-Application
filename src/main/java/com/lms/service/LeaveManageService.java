package com.lms.service;

import com.lms.models.AbstractEmployee;
import com.lms.models.LeaveDetails;
import com.lms.models.LeaveType;
import com.lms.models.UserLeaveBalance;
import com.lms.repository.EmployeeRepository;
import com.lms.repository.LeaveManageNativeSqlRepo;
import com.lms.repository.LeaveManageRepository;
import com.lms.repository.LeaveTypeRepository;
import com.lms.repository.UserLeaveBalanceRepository;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer to handle operations related to leave management such as applying, approving,
 * rejecting leaves, and fetching leave-related data.
 */
@Service
public class LeaveManageService {

  private static final Logger log = LoggerFactory.getLogger(LeaveManageService.class);
  private final LeaveManageRepository leaveManageRepository;
  private final LeaveManageNativeSqlRepo leaveManageNativeRepo;
  private final UserLeaveBalanceRepository userLeaveBalanceRepository;
  private final EmployeeRepository employeeRepository;
  private final LeaveTypeRepository leaveTypeRepository;

  @Autowired
  public LeaveManageService(LeaveManageRepository leaveManageRepository,
      LeaveManageNativeSqlRepo leaveManageNativeRepo,
      UserLeaveBalanceRepository userLeaveBalanceRepository,
      EmployeeRepository employeeRepository,
      LeaveTypeRepository leaveTypeRepository) {
    this.leaveManageRepository = leaveManageRepository;
    this.leaveManageNativeRepo = leaveManageNativeRepo;
    this.userLeaveBalanceRepository = userLeaveBalanceRepository;
    this.employeeRepository = employeeRepository;
    this.leaveTypeRepository = leaveTypeRepository;
  }

  /**
   * Applies for a leave by setting up necessary details and saving the leave record.
   *
   * @param leaveDetails the details of the leave being applied for
   */
  @Transactional
  public void applyLeave(LeaveDetails leaveDetails) {
    if (leaveDetails.getFromDate() == null || leaveDetails.getToDate() == null) {
      throw new IllegalStateException("Both from date and to date must be set.");
    }
    long duration = ChronoUnit.DAYS.between(leaveDetails.getFromDate(), leaveDetails.getToDate()) + 1;
    leaveDetails.setDuration((int) duration);

    // Check user and leave type are set
    if (leaveDetails.getUser() == null || leaveDetails.getLeaveType() == null) {
      throw new IllegalStateException("User and leave type must be set.");
    }

    // Retrieve and check the leave balance
    UserLeaveBalance balance = userLeaveBalanceRepository.findByUser_IdAndLeaveType_IdAndYear(
            leaveDetails.getUser().getId(), leaveDetails.getLeaveType().getId(), Year.now().getValue())
        .orElseThrow(() -> new RuntimeException("Balance not found"));

    if (balance.getCurrentBalance() < leaveDetails.getDuration()) {
      throw new IllegalStateException("Insufficient leave balance");
    }

    // Set the leave request status and save it
    leaveDetails.setStatus("PENDING");
    leaveManageRepository.save(leaveDetails);
  }

  /**
   * Approves a leave request and updates the leave balance.
   *
   * @param leaveId the ID of the leave to approve
   */
  @Transactional
  public void approveLeave(Integer leaveId) {
    LeaveDetails leaveDetails = leaveManageRepository.findById(leaveId)
        .orElseThrow(() -> new RuntimeException("Leave request not found"));
    UserLeaveBalance balance = userLeaveBalanceRepository.findByUser_IdAndLeaveType_IdAndYear(
            leaveDetails.getUser().getId(), leaveDetails.getLeaveType().getId(), Year.now().getValue())
        .orElseThrow(() -> new RuntimeException("Balance not found"));

    if (balance.getCurrentBalance() < leaveDetails.getDuration()) {
      throw new IllegalStateException("Insufficient leave balance");
    }

    // Decrement the leave balance
    balance.setCurrentBalance(balance.getCurrentBalance() - leaveDetails.getDuration());
    userLeaveBalanceRepository.save(balance);

    leaveDetails.setStatus("APPROVED");
    leaveManageRepository.save(leaveDetails);
  }

  /**
   * Rejects a leave request and updates the leave status.
   *
   * @param leaveId the ID of the leave to reject
   */
  @Transactional
  public void rejectLeave(Integer leaveId) {
    LeaveDetails leaveDetails = leaveManageRepository.findById(leaveId)
        .orElseThrow(() -> new RuntimeException("Leave request not found"));
    leaveDetails.setStatus("REJECTED");
    leaveManageRepository.save(leaveDetails);
  }

  /**
   * Retrieves all leave details from the database.
   *
   * @return a list of all leave details
   */
  public List<LeaveDetails> getAllLeaves() {
    return leaveManageRepository.findAll();
  }

  /**
   * Retrieves all active leave details from the database.
   *
   * @return a list of all active leave details
   */
  public List<LeaveDetails> getAllActiveLeaves() {
    return leaveManageRepository.getAllActiveLeaves();
  }

  /**
   * Retrieves all leaves of a specific user.
   *
   * @param username the email of the user
   * @return a list of all leaves associated with the user
   */
  public List<LeaveDetails> getAllLeavesOfUser(String username) {
    return leaveManageRepository.getAllLeavesOfUser(username);
  }

  /**
   * Retrieves all leaves matching a specific status.
   *
   * @param status the status to filter leaves
   * @return a list of all leaves matching the specified status
   */
  public List<LeaveDetails> getAllLeavesOnStatus(String status) {
    if (status == null) {
      // No specific status requested, return all leaves
      return leaveManageRepository.findAll();
    } else {
      // Return leaves matching the specific status
      return leaveManageNativeRepo.getAllLeavesOnStatus(status);
    }
  }

  /**
   * Retrieves all leaves of a specific user filtered by status.
   *
   * @param email the email of the user
   * @param status the status to filter leaves
   * @return a list of leaves filtered by the specified status
   */
  public List<LeaveDetails> getAllLeavesOfUserByStatus(String email, String status) {
    if (status == null || status.isEmpty()) {
      return leaveManageRepository.findByUserEmail(email);
    } else {
      return leaveManageRepository.findByUserEmailAndStatus(email, status);
    }
  }

  /**
   * Finds leaves managed by a specific manager.
   *
   * @param managerEmail the email of the manager
   * @return a list of leaves managed by the specified manager
   */
  @Transactional
  public List<LeaveDetails> findLeavesByManager(String managerEmail) {
    Optional<AbstractEmployee> managerOpt = employeeRepository.findByEmail(managerEmail);
    if (!managerOpt.isPresent()) {
      throw new IllegalStateException("Manager not found with email: " + managerEmail);
    }
    AbstractEmployee manager = managerOpt.get();
    List<AbstractEmployee> subordinates = employeeRepository.findByManagerId(manager.getId());
    List<Integer> subordinateIds = subordinates.stream().map(AbstractEmployee::getId).collect(
        Collectors.toList());
    return leaveManageRepository.findAllByUserIdIn(subordinateIds);
  }

  /**
   * Retrieves the leave balances for a specific user.
   *
   * @param userId the ID of the user
   * @return a list of leave balances for the user
   */
  public List<UserLeaveBalance> getUserLeaveBalances(Integer userId) {
    return userLeaveBalanceRepository.findByUser_Id(userId);
  }

  /**
   * Retrieves all leave types available in the system.
   *
   * @return a list of all leave types
   */
  public List<LeaveType> getAllLeaveTypes() {
    return leaveTypeRepository.findAll();
  }
}

package com.lms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.models.UserLeaveBalance;
import com.lms.models.UserLeaveBalanceId;

/**
 * Repository interface for managing user leave balances within the LeaveLog application.
 * It extends JpaRepository to utilize standard data access functionalities for entities
 * identified by composite keys of type UserLeaveBalanceId.
 */
@Repository
public interface UserLeaveBalanceRepository extends JpaRepository<UserLeaveBalance, UserLeaveBalanceId> {

  /**
   * Retrieves all leave balances for a specific user identified by their user ID.
   *
   * @param userId the ID of the user whose leave balances are to be retrieved
   * @return a list of UserLeaveBalance objects associated with the specified user
   */
  List<UserLeaveBalance> findByUser_Id(Integer userId);

  /**
   * Finds a specific leave balance entry based on user ID, leave type ID, and the year.
   *
   * @param userId the ID of the user
   * @param leaveTypeId the ID of the leave type
   * @param year the year for which the leave balance is applicable
   * @return an Optional containing the UserLeaveBalance if found, or an empty Optional if no matching balance is found
   */
  Optional<UserLeaveBalance> findByUser_IdAndLeaveType_IdAndYear(Integer userId, Integer leaveTypeId, Integer year);

}

package com.lms.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lms.models.LeaveDetails;

/**
 * Spring Data repository for handling CRUD operations and custom queries related to leave details.
 * Extends JpaRepository to leverage built-in JPA functionalities for entity management.
 */
@Repository
public interface LeaveManageRepository extends JpaRepository<LeaveDetails, Integer> {

  /**
   * Finds all leave details for a list of user IDs.
   *
   * @param userIds the list of user IDs to find leave details for
   * @return a list of LeaveDetails corresponding to the given user IDs
   */
  List<LeaveDetails> findAllByUserIdIn(List<Integer> userIds);

  /**
   * Finds all leave details associated with a specific user's email.
   *
   * @param email the email of the user
   * @return a list of LeaveDetails associated with the given email
   */
  List<LeaveDetails> findByUserEmail(String email);

  /**
   * Finds all leave details associated with a specific user's email and leave status.
   *
   * @param email the user's email
   * @param status the status of the leaves
   * @return a list of LeaveDetails that match the given email and status
   */
  List<LeaveDetails> findByUserEmailAndStatus(String email, String status);

  /**
   * Retrieves all active leave records.
   *
   * @return a list of active LeaveDetails
   */
  @Query("SELECT ld FROM LeaveDetails ld WHERE ld.active = true")
  List<LeaveDetails> getAllActiveLeaves();

  /**
   * Retrieves all leave records for a specific user, sorted by their ID in descending order.
   * This query joins the leave details with the leave type for comprehensive data retrieval.
   *
   * @param username the email of the user to find leaves for
   * @return a list of LeaveDetails for the specified user
   */
  @Query("SELECT ld FROM LeaveDetails ld JOIN ld.leaveType lt WHERE ld.user.email = :username ORDER BY ld.id DESC")
  List<LeaveDetails> getAllLeavesOfUser(String username);
}

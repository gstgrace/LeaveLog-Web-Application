package com.lms.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.lms.models.AbstractEmployee;

/**
 * Repository interface for handling database operations for the AbstractEmployee entity.
 * Extends JpaRepository to provide basic CRUD operations and includes custom queries for
 * employee-specific retrievals and modifications.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<AbstractEmployee, Integer> {

  /**
   * Finds an employee by their email address.
   *
   * @param email the email to search for
   * @return an Optional containing the found employee or an empty Optional if no employee is found
   */
  Optional<AbstractEmployee> findByEmail(String email);

  /**
   * Retrieves all employees managed by a specific manager.
   *
   * @param managerId the ID of the manager
   * @return a list of employees under the specified manager
   */
  List<AbstractEmployee> findByManagerId(Integer managerId);

  /**
   * Retrieves all employees sorted by their ID in ascending order.
   *
   * @return a list of all employees sorted by ID
   */
  List<AbstractEmployee> findAllByOrderByIdAsc();

  /**
   * Finds employees by their role.
   *
   * @param role the role to filter employees by
   * @return a list of employees who have the specified role
   */
  List<AbstractEmployee> findByRole(String role);

  /**
   * Blocks an employee by setting their active status to false.
   *
   * @param id the ID of the employee to block
   */
  @Transactional
  @Modifying
  @Query("UPDATE AbstractEmployee e SET e.active = false WHERE e.id = :id")
  void blockEmployeeById(Integer id);

  /**
   * Unblocks an employee by setting their active status to true.
   *
   * @param id the ID of the employee to unblock
   */
  @Transactional
  @Modifying
  @Query("UPDATE AbstractEmployee e SET e.active = true WHERE e.id = :id")
  void unblockEmployeeById(Integer id);

}

package com.lms.service;

import com.lms.models.AbstractEmployee;
import com.lms.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

/**
 * Service for managing operations by managers.
 * This includes finding, deleting, and modifying the status of employees.
 */
@Service
public class ManagerService {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public ManagerService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  /**
   * Retrieves an employee by their unique identifier.
   *
   * @param id the ID of the employee
   * @return an Optional containing the found employee or an empty Optional if no employee is found
   */
  public Optional<AbstractEmployee> getEmployeeById(int id) {
    return employeeRepository.findById(id);
  }

  /**
   * Finds all direct reports of a manager specified by the manager's email.
   *
   * @param managerEmail the email of the manager
   * @return a list of employees who report directly to the specified manager
   * @throws IllegalStateException if the manager with the specified email does not exist
   */
  @Transactional
  public List<AbstractEmployee> findDirectReportsByManager(String managerEmail) {
    Optional<AbstractEmployee> managerOpt = employeeRepository.findByEmail(managerEmail);
    if (!managerOpt.isPresent()) {
      throw new IllegalStateException("Manager not found with email: " + managerEmail);
    }
    AbstractEmployee manager = managerOpt.get();
    return employeeRepository.findByManagerId(manager.getId());
  }

  /**
   * Deletes an employee from the system by their ID.
   *
   * @param id the ID of the employee to delete
   */
  public void deleteEmployee(int id) {
    employeeRepository.deleteById(id);
  }

  /**
   * Blocks an employee, preventing them from logging into the system or taking certain actions.
   *
   * @param id the ID of the employee to block
   */
  public void blockEmployee(int id) {
    employeeRepository.blockEmployeeById(id);
  }

  /**
   * Unblocks an employee, restoring their access to the system.
   *
   * @param id the ID of the employee to unblock
   */
  public void unblockEmployee(int id) {
    employeeRepository.unblockEmployeeById(id);
  }

}

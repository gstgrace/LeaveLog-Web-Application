package com.lms.service;

import com.lms.models.AbstractEmployee;
import com.lms.models.Employee;
import com.lms.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing employee-related operations such as retrieving,
 * saving, and initializing leave balances for employees. It interacts with
 * the EmployeeRepository and uses PasswordEncoder for hashing passwords.
 */
@Service
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final PasswordEncoder passwordEncoder;
  private final LeaveBalanceService leaveBalanceService;

  @Autowired
  public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, LeaveBalanceService leaveBalanceService) {
    this.employeeRepository = employeeRepository;
    this.passwordEncoder = passwordEncoder;
    this.leaveBalanceService = leaveBalanceService;
  }

  /**
   * Retrieves all employees with the role of "Manager".
   *
   * @return a list of all managers
   */
  public List<AbstractEmployee> findAllManagers() {
    return employeeRepository.findByRole("Manager");
  }

  /**
   * Finds an employee by their ID.
   *
   * @param id the employee's ID
   * @return an Optional containing the found employee or an empty Optional if no employee is found
   */
  public Optional<AbstractEmployee> findEmployeeById(int id) {
    return employeeRepository.findById(id);
  }

  /**
   * Retrieves the currently authenticated employee from the security context.
   *
   * @return the currently authenticated employee, or null if not found
   */
  public Employee getCurrentEmployee() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    return findEmployeeByEmail(email).orElse(null);
  }

  /**
   * Finds an employee by their email address.
   *
   * @param email the email of the employee
   * @return an Optional containing the found employee or an empty Optional if no employee is found
   */
  public Optional<AbstractEmployee> findEmployeeByEmail(String email) {
    return employeeRepository.findByEmail(email);
  }

  /**
   * Saves an existing employee after encoding their password.
   *
   * @param employee the employee to save
   */
  public void saveExistingEmployee(AbstractEmployee employee) {
    employee.setPassword(passwordEncoder.encode(employee.getPassword()));
    employeeRepository.save(employee);
  }

  /**
   * Saves a new employee and initializes their leave balances.
   *
   * @param employee the new employee to save
   */
  public void saveNewEmployee(AbstractEmployee employee) {
    employee.setPassword(passwordEncoder.encode(employee.getPassword()));
    employeeRepository.save(employee);
    leaveBalanceService.initializeLeaveBalancesForNewUser(employee);
  }
}

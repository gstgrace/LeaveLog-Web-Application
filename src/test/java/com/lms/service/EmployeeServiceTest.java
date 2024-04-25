package com.lms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.lms.models.AbstractEmployee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lms.models.Manager;
import com.lms.models.NonManagerEmployee;
import com.lms.repository.EmployeeRepository;
import com.lms.service.EmployeeService;
import com.lms.service.LeaveBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

class EmployeeServiceTest {

  private EmployeeService employeeService;

  private EmployeeRepository employeeRepository;
  private PasswordEncoder passwordEncoder;
  private LeaveBalanceService leaveBalanceService;

  @BeforeEach
  void setUp() {
    employeeRepository = mock(EmployeeRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    leaveBalanceService = mock(LeaveBalanceService.class);
    employeeService = new EmployeeService(employeeRepository, passwordEncoder, leaveBalanceService);
  }

  @Test
  void testFindEmployeeById() {
    // Given
    int id = 1;
    Manager employee = new Manager();
    when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

    // When
    Optional<AbstractEmployee> foundEmployee = employeeService.findEmployeeById(id);

    // Then
    assertThat(foundEmployee).isPresent();
    assertThat(foundEmployee.get()).isEqualTo(employee);
  }


  @Test
  void testFindEmployeeByEmail() {
    // Given
    String email = "test@example.com";
    Manager employee = new Manager();
    when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

    // When
    Optional<AbstractEmployee> foundEmployee = employeeService.findEmployeeByEmail(email);

    // Then
    assertThat(foundEmployee).isPresent();
    assertThat(foundEmployee.get()).isEqualTo(employee);
  }

}

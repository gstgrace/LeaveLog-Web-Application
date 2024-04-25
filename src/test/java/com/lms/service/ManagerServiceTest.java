package com.lms.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.lms.models.AbstractEmployee;
import com.lms.models.Manager;
import com.lms.repository.EmployeeRepository;
import com.lms.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ManagerServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private ManagerService managerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetEmployeeById() {
    // Given
    int employeeId = 1;
    Manager mockEmployee = new Manager(); // Assuming Manager extends AbstractEmployee
    when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(mockEmployee));

    // When
    Optional<AbstractEmployee> result = managerService.getEmployeeById(employeeId);

    // Then
    assertTrue(result.isPresent());
    assertEquals(mockEmployee, result.get());
    verify(employeeRepository).findById(employeeId);
  }

  @Test
  void testFindDirectReportsByManager() {
    // Given
    String managerEmail = "manager@example.com";
    int managerId = 1;
    Manager mockManager = new Manager();
    mockManager.setId(managerId);
    when(employeeRepository.findByEmail(managerEmail)).thenReturn(Optional.of(mockManager));

    List<AbstractEmployee> mockDirectReports = Collections.emptyList();
    when(employeeRepository.findByManagerId(managerId)).thenReturn(mockDirectReports);

    // When
    List<AbstractEmployee> result = managerService.findDirectReportsByManager(managerEmail);

    // Then
    assertEquals(mockDirectReports, result);
    verify(employeeRepository).findByEmail(managerEmail);
    verify(employeeRepository).findByManagerId(managerId);
  }

  // Add more test cases as needed for other methods

}

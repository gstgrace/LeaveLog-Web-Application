package com.lms.controllers;
import com.lms.models.AbstractEmployee;
import com.lms.models.NonManagerEmployee;
import com.lms.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;

class LoginControllerTest {

  @Mock
  private EmployeeService employeeService;

  @Mock
  private LeaveManageService leaveManageService;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectAttributes;

  @InjectMocks
  private LoginController loginController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateNewUser() {
    String role = "Employee"; // Adjusted role to match the converter mapping
    String email = "test@example.com";
    String firstName = "John";
    String lastName = "Doe";
    String password = "password";
    LocalDate onboardingDate = LocalDate.now();
    Integer managerId = 1;

    NonManagerEmployee employee = new NonManagerEmployee();
    employee.setEmail(email);
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setPassword(password);
    employee.setOnboardingDate(onboardingDate);

    when(employeeService.findEmployeeByEmail(email)).thenReturn(Optional.empty());
    when(employeeService.findEmployeeById(managerId)).thenReturn(Optional.empty());

    String result = loginController.createNewUser(role, email, firstName, lastName, password,
        onboardingDate, managerId, model, redirectAttributes);

    verify(employeeService, times(1)).saveNewEmployee(any(AbstractEmployee.class));
    verify(redirectAttributes, times(1)).addFlashAttribute(eq("successMessage"), anyString());
    assert result.equals("redirect:/login");
  }
}

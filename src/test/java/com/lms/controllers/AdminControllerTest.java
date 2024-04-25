package com.lms.controllers;

import com.lms.models.AbstractEmployee;
import com.lms.models.Manager;
import com.lms.models.NonManagerEmployee;
import com.lms.repository.EmployeeRepository;
import com.lms.service.EmployeeService;
import com.lms.service.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private EmployeeService employeeService;

  @MockBean
  private ManagerService managerService;

  @MockBean
  private EmployeeRepository employeeRepository;

  private Manager testManager;

  @BeforeEach
  public void setUp() {
    testManager = new Manager();
    testManager.setId(1);
    testManager.setEmail("manager1@email.com");
    testManager.setFirstName("Manager");
    testManager.setLastName("One");
    testManager.setRole("MANAGER");
    testManager.setActive(true);
    testManager.setOnboardingDate(java.time.LocalDate.now());
    // Setup the specific behavior if needed
    when(employeeRepository.findById(1)).thenReturn(Optional.of(testManager));
  }

  @Test
  @WithMockUser(username = "manager1@email.com", authorities = "Manager")
  public void showManageUsers_WhenCalled_PopulatesModelAndReturnsManageUsersView() throws Exception {
    List<AbstractEmployee> employees = Arrays.asList(testManager); // Use AbstractEmployee as the list type
    when(managerService.findDirectReportsByManager(anyString())).thenReturn(employees);

    mockMvc.perform(get("/user/manage-users"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("users"))
        .andExpect(view().name("manageUsers"));
  }

  @Test
  @WithMockUser(username = "manager1@email.com", authorities = "Manager")
  public void manageUsers_WhenCalledWithEditAction_ReturnsEditUserView() throws Exception {
    when(managerService.getEmployeeById(testManager.getId())).thenReturn(Optional.of(testManager));

    mockMvc.perform(get("/user/manage-users/edit/" + testManager.getId()))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("userInfo"))
        .andExpect(view().name("editUser"));
  }

  @Test
  @WithMockUser(username = "manager1@email.com", authorities = {"Manager"})
  public void blockUser_WhenCalledWithManagerRole_PerformsBlocking() throws Exception {
    int userId = 1;
    AbstractEmployee testEmployee = new Manager();
    testEmployee.setId(userId);

    when(managerService.getEmployeeById(userId)).thenReturn(Optional.of(testEmployee));
    doNothing().when(managerService).blockEmployee(userId);

    mockMvc.perform(get("/user/manage-users/block/" + userId))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/user/manage-users"))
        .andExpect(flash().attributeExists("successMessage"));

    verify(managerService).blockEmployee(userId);
  }
}


package com.lms.controllers;

import com.lms.models.LeaveDetails;
import com.lms.models.LeaveType;
import com.lms.models.Manager;
import com.lms.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class LeaveManageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Mock
  private LeaveManageService leaveManageService;

  @Mock
  private EmployeeService employeeService;

  @Mock
  private BindingResult bindingResult;

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
    testManager.setOnboardingDate(LocalDate.now());
  }

  @Test
  @WithMockUser(username = "manager1@email.com", authorities = "Manager")
  public void applyLeave_SubmitValidLeaveDetails_RedirectsWithSuccess() throws Exception {
    LeaveDetails leaveDetails = new LeaveDetails();
    leaveDetails.setFromDate(LocalDate.now());
    leaveDetails.setToDate(LocalDate.now().plusDays(5));
    leaveDetails.setReason("Unit test");
    LeaveType leaveType = new LeaveType();
    leaveDetails.setLeaveType(leaveType);
    leaveDetails.setUser(testManager);

    given(employeeService.findEmployeeByEmail("manager1@email.com")).willReturn(Optional.of(testManager));
    when(bindingResult.hasErrors()).thenReturn(false);

    mockMvc.perform(post("/user/apply-leave")
            .flashAttr("leaveDetails", leaveDetails)
            .param("leaveType", "1"))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/user/home"))
        .andExpect(flash().attributeExists("successMessage"));

  }
}
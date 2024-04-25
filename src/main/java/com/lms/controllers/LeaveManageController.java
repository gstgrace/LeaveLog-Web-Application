package com.lms.controllers;

import com.lms.models.AbstractEmployee;
import com.lms.models.Employee;
import com.lms.models.LeaveDetails;
import com.lms.models.LeaveType;
import com.lms.models.UserLeaveBalance;
import com.lms.service.EmployeeService;
import com.lms.service.LeaveManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing leave-related operations within the LeaveLog application.
 * It handles functionalities such as applying for leaves, managing and reviewing leave requests,
 * and querying leave balances and types.
 */
@Controller
@RequestMapping("/user")
public class LeaveManageController {

  private final LeaveManageService leaveManageService;
  private final EmployeeService employeeService;
  private final Validator validator;
  private static final Logger log = LoggerFactory.getLogger(LeaveManageController.class);

  /**
   * Constructs a LeaveManageController with necessary services and validator.
   *
   * @param leaveManageService the service for managing leave operations
   * @param employeeService the service for employee data operations
   * @param validator the validator for validating leave details
   */
  @Autowired
  public LeaveManageController(LeaveManageService leaveManageService, EmployeeService employeeService, Validator validator) {
    this.leaveManageService = leaveManageService;
    this.employeeService = employeeService;
    this.validator = validator;
  }

  /**
   * Displays the form for an employee to apply for leave, fetching necessary data like leave balances.
   *
   * @param model the UI model to carry data for rendering views
   * @return the view name for applying leave
   */
  @GetMapping("/apply-leave")
  public String applyLeave(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Optional<AbstractEmployee> optionalEmployee = employeeService.findEmployeeByEmail(email);

    if (optionalEmployee.isPresent()) {
      Employee employee = optionalEmployee.get();
      List<UserLeaveBalance> balances = leaveManageService.getUserLeaveBalances(employee.getId());
      model.addAttribute("balances", balances);
      model.addAttribute("employee", employee);
    } else {
      model.addAttribute("errorMessage", "Employee details not found!");
      return "redirect:/login";
    }
    model.addAttribute("leaveDetails", new LeaveDetails());
    List<LeaveType> leaveTypes = leaveManageService.getAllLeaveTypes();
    model.addAttribute("leaveTypes", leaveTypes);
    return "ApplyLeave";
  }

  /**
   * Processes the submission of a leave application form, validating and storing the leave details.
   *
   * @param leaveDetails the leave details submitted by the employee
   * @param bindingResult the result of binding the form to the model, containing any errors
   * @param model the UI model to carry data for rendering views
   * @param redirectAttributes attributes for passing messages on redirection
   * @return the redirect view name after processing the leave application
   */
  @PostMapping("/apply-leave")
  public String submitApplyLeave(@ModelAttribute LeaveDetails leaveDetails, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Optional<AbstractEmployee> optionalEmployee = employeeService.findEmployeeByEmail(email);

    if (optionalEmployee.isPresent()) {
      Employee employee = optionalEmployee.get();
      leaveDetails.setUser(employee);
      leaveDetails.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
      leaveDetails.setActive(true);
      leaveDetails.setStatus("PENDING");
    } else {
      redirectAttributes.addFlashAttribute("errorMessage", "Error fetching user information.");
      return "redirect:/user/apply-leave";
    }

    validator.validate(leaveDetails, bindingResult);

    if (bindingResult.hasErrors()) {
      List<LeaveType> leaveTypes = leaveManageService.getAllLeaveTypes();
      model.addAttribute("leaveTypes", leaveTypes);
      model.addAttribute("leaveDetails", leaveDetails);
      return "ApplyLeave";
    }

    leaveManageService.applyLeave(leaveDetails);
    redirectAttributes.addFlashAttribute("successMessage", "Your Leave Request has been submitted successfully!");
    return "redirect:/user/home";
  }

  /**
   * Retrieves all leaves with a specific status or all statuses if not specified.
   *
   * @param status the optional status to filter leaves
   * @return a list of LeaveDetails meeting the status criteria
   */
  @GetMapping("/get-all-leaves")
  @ResponseBody
  public List<LeaveDetails> getAllLeaves(@RequestParam Optional<String> status) {
    return leaveManageService.getAllLeavesOnStatus(status.orElse(null));
  }

  /**
   * Allows managers to view and manage leave requests submitted by their direct reports.
   * This method is secured to be accessible only by users with 'Manager' authority.
   *
   * @param model the UI model to carry data for rendering views
   * @return the view name for managing leaves
   */
  @PreAuthorize("hasAuthority('Manager')")
  @GetMapping("/manage-leaves")
  public String manageLeaves(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    List<LeaveDetails> leavesList = leaveManageService.findLeavesByManager(email);
    model.addAttribute("leavesList", leavesList);
    return "ManageLeaves";
  }

  /**
   * Approves a leave request identified by its ID.
   *
   * @param id the ID of the leave to approve
   * @param redirectAttributes attributes for passing messages on redirection
   * @return the redirect view name after processing the approval
   */
  @GetMapping("/manage-leaves/accept/{id}")
  public String acceptLeave(@PathVariable int id, RedirectAttributes redirectAttributes) {
    try {
      leaveManageService.approveLeave(id);
      redirectAttributes.addFlashAttribute("successMessage", "Leave approved successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve leave: " + e.getMessage());
    }
    return "redirect:/user/manage-leaves";
  }

  /**
   * Rejects a leave request identified by its ID.
   *
   * @param id the ID of the leave to reject
   * @param redirectAttributes attributes for passing messages on redirection
   * @return the redirect view name after processing the rejection
   */
  @GetMapping("/manage-leaves/reject/{id}")
  public String rejectLeave(@PathVariable int id, RedirectAttributes redirectAttributes) {
    try {
      leaveManageService.rejectLeave(id);
      redirectAttributes.addFlashAttribute("successMessage", "Leave rejected successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject leave: " + e.getMessage());
    }
    return "redirect:/user/manage-leaves";
  }

  /**
   * Displays the leave records of the logged-in user, filtered by status if specified.
   *
   * @param status the optional status to filter the user's leaves
   * @param model the UI model to carry data for rendering views
   * @return the view name for displaying user-specific leaves
   */
  @GetMapping("/my-leaves")
  public String showMyLeaves(@RequestParam Optional<String> status, Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();

    List<LeaveDetails> leavesList = leaveManageService.getAllLeavesOfUserByStatus(email, status.orElse(null));

    model.addAttribute("leavesList", leavesList);
    model.addAttribute("selectedStatus", status.orElse("ALL"));
    return "MyLeaves";
  }
}

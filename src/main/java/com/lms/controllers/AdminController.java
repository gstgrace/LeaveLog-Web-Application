package com.lms.controllers;

import com.lms.models.AbstractEmployee;
import com.lms.models.Employee;
import com.lms.service.EmployeeService;
import com.lms.service.ManagerService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling administrative functionalities related to LeaveLog's employee management
 * such as changing passwords, managing user details, and handling user permissions within
 * the LeaveLog application. This class interfaces with both EmployeeService and ManagerService
 * to perform its operations.
 */
@Controller
public class AdminController {

  private final EmployeeService employeeService;
  private final ManagerService managerService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
   * Constructs an AdminController with necessary services.
   *
   * @param employeeService the service for employee data operations
   * @param managerService the service for manager-specific operations
   */
  @Autowired
  public AdminController(EmployeeService employeeService, ManagerService managerService) {
    this.employeeService = employeeService;
    this.managerService = managerService;
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
  }

  /**
   * Displays the password change form.
   *
   * @return the view name for changing password
   */
  @GetMapping("/user/change-password")
  public String changePasswordForm() {
    return "changePassword";
  }

  /**
   * Processes the password change request. Validates current password and updates with new one if valid.
   *
   * @param currentPassword the current password provided by the user
   * @param newPassword the new password to set
   * @param model the UI model to carry data
   * @param redirectAttributes attributes for passing messages on redirection
   * @return the redirect view name after processing the password change
   */
  @PostMapping("/user/change-password")
  public String changePasswordSubmit(@RequestParam("currentPassword") String currentPassword,
      @RequestParam("newPassword") String newPassword, Model model,
      RedirectAttributes redirectAttributes) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    Optional<AbstractEmployee> optionalEmployee = employeeService.findEmployeeByEmail(username);
    if (!optionalEmployee.isPresent()) {
      redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
      return "redirect:/user/change-password";
    }
    Employee employee = optionalEmployee.get();
    if (!bCryptPasswordEncoder.matches(currentPassword, employee.getPassword())) {
      redirectAttributes.addFlashAttribute("errorMessage", "Current Password entered is wrong!!!");
      return "redirect:/user/change-password";
    }
    employee.setPassword(bCryptPasswordEncoder.encode(newPassword));
    employeeService.saveExistingEmployee((AbstractEmployee) employee);
    redirectAttributes.addFlashAttribute("successMessage", "Password changed Successfully!!!");
    return "redirect:/user/home";
  }

  /**
   * Displays the user management interface for managers. This method is secured to be accessible
   * only by users with 'Manager' authority.
   *
   * @param model the UI model to carry data
   * @return the view name for managing users
   */
  @PreAuthorize("hasAuthority('Manager')")
  @GetMapping("/user/manage-users")
  public String showManageUsers(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String managerEmail = auth.getName();  // Manager's email as username
    List<AbstractEmployee> directReports = managerService.findDirectReportsByManager(managerEmail);
    model.addAttribute("users", directReports);
    return "manageUsers";
  }

  /**
   * Handles actions like editing, deleting, blocking, or unblocking a user based on the provided
   * path variable 'action'. Redirects with appropriate messages.
   *
   * @param action the action to perform ('edit', 'delete', 'block', 'unblock')
   * @param id the ID of the employee to manage
   * @param model the UI model to carry data
   * @param redirectAttributes attributes for passing messages on redirection
   * @return the redirect view name after action
   */
  @GetMapping("/user/manage-users/{action}/{id}")
  public String manageUsers(@PathVariable("action") String action, @PathVariable("id") int id,
      Model model, RedirectAttributes redirectAttributes) {
    Optional<AbstractEmployee> optionalEmployee = managerService.getEmployeeById(id);
    if (!optionalEmployee.isPresent()) {
      redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
      return "redirect:/user/manage-users";
    }
    Employee employee = optionalEmployee.get();
    switch (action) {
      case "edit":
        model.addAttribute("userInfo", employee);
        return "editUser";
      case "delete":
        managerService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "User removed successfully!!");
        break;
      case "block":
        managerService.blockEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "User blocked successfully!!");
        break;
      case "unblock":
        managerService.unblockEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "User is active now!!");
        break;
    }
    return "redirect:/user/manage-users";
  }

  /**
   * Saves changes to an employee's details after editing. Validates input and handles errors appropriately.
   *
   * @param employee the employee details to save
   * @param bindResult the binding result to capture validation errors
   * @param model the UI model to carry data
   * @param redirectAttributes attributes for passing messages on redirection
   * @param onboardingDate the updated onboarding date
   * @return the redirect view name after saving changes
   */
  @PostMapping("/user/manage-users/save-user-edit")
  public String saveUserEdit(@Valid @ModelAttribute("userInfo") Employee employee,
      BindingResult bindResult,
      Model model,
      RedirectAttributes redirectAttributes,
      @RequestParam("onboardingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate onboardingDate) {

    if (bindResult.hasErrors()) {
      model.addAttribute("userInfo", employee);
      return "editUser";
    }

    employee.setOnboardingDate(onboardingDate);
    employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));
    employeeService.saveExistingEmployee((AbstractEmployee) employee);
    redirectAttributes.addFlashAttribute("successMessage", "User Details updated successfully!!");
    return "redirect:/user/manage-users";
  }
}

package com.lms.controllers;

import com.lms.configuration.StringToEmployeeConverter;
import com.lms.models.AbstractEmployee;
import com.lms.models.Employee;
import com.lms.models.NonManagerEmployee;
import com.lms.models.UserLeaveBalance;
import com.lms.service.EmployeeService;
import com.lms.service.GeminiApiClient;
import com.lms.service.LeaveManageService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling login, registration, and user home functionalities.
 * This controller supports operations like user authentication, new user registration,
 * and display of the user-specific home page.
 */
@Controller
@RequestMapping("/")
public class LoginController {

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private LeaveManageService leaveManageService;

  @Autowired
  private Validator validator;

  @Autowired
  private GeminiApiClient geminiApiClient;

  private static final Logger log = LoggerFactory.getLogger(LoginController.class);

  /**
   * Displays the login page unless the user is already authenticated, in which case
   * they are redirected to the home page.
   *
   * @param model the UI model to carry data for rendering views
   * @return the view name for the login page or a redirection to the home page
   */
  @GetMapping({"/", "/login"})
  public String login(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof AnonymousAuthenticationToken)) {
      return "redirect:/user/home";
    }
    return "Login";
  }

  /**
   * Displays the registration form for new users, including a list of managers if applicable.
   *
   * @param model the UI model to carry data for rendering views
   * @return the view name for the registration page
   */
  @GetMapping("/registration")
  public String registration(Model model) {
    List<AbstractEmployee> managers = employeeService.findAllManagers();
    model.addAttribute("managers", managers);
    model.addAttribute("employee", new NonManagerEmployee());
    return "Registration";
  }

  /**
   * Processes the registration of a new user, handling input validation and potential errors.
   * Creates a new employee based on the specified role and other form data.
   *
   * @param role the role of the employee to create
   * @param email the email of the employee
   * @param firstName the first name of the employee
   * @param lastName the last name of the employee
   * @param password the password for the employee's account
   * @param onboardingDate the date the employee is onboarded
   * @param managerId the optional ID of the manager if applicable
   * @param model the UI model to carry data for rendering views
   * @param redirectAttributes attributes for passing messages on redirection
   * @return the redirect view name after processing the registration or a redirection back to the registration form if errors occur
   */
  @PostMapping("/registration")
  public String createNewUser(@RequestParam String role, @RequestParam String email,
      @RequestParam String firstName, @RequestParam String lastName,
      @RequestParam String password,
      @RequestParam("onboardingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate onboardingDate,
      @RequestParam(required = false) Integer managerId,
      Model model, RedirectAttributes redirectAttributes) {
    Employee employee = new StringToEmployeeConverter().convert(role);

    employee.setEmail(email);
    employee.setFirstName(firstName);
    employee.setLastName(lastName);
    employee.setPassword(password);  // Assume password encoding handled elsewhere
    employee.setOnboardingDate(onboardingDate);

    if (managerId != null) {
      Optional<AbstractEmployee> manager = employeeService.findEmployeeById(managerId);
      manager.ifPresent(employee::setManager);
    }

    if (employeeService.findEmployeeByEmail(email).isPresent()) {
      model.addAttribute("errorMessage", "An account already exists with this Email");
      model.addAttribute("managers", employeeService.findAllManagers());
      model.addAttribute("employee", employee);
      return "Registration";
    }

    try {
      employeeService.saveNewEmployee((AbstractEmployee) employee);
      redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Awaiting approval.");
      return "redirect:/login";
    } catch (Exception e) {
      log.error("Error while registering employee", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Failed to register.");
      return "redirect:/registration";
    }
  }

  /**
   * Displays the home page for an authenticated user, including leave balances and
   * well-being tips fetched via an API client.
   *
   * @param request the HttpServletRequest to manage session attributes
   * @param model the UI model to carry data for rendering views
   * @return the view name for the home page
   */
  @GetMapping("/user/home")
  public String home(HttpServletRequest request, Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Optional<AbstractEmployee> employee = employeeService.findEmployeeByEmail(email);

    if (employee.isPresent()) {
      List<UserLeaveBalance> balances = leaveManageService.getUserLeaveBalances(employee.get().getId());
      model.addAttribute("balances", balances);
      request.getSession().setAttribute("employee", employee.get());
      model.addAttribute("employee", employee.get());
    } else {
      model.addAttribute("errorMessage", "Employee details not found!");
    }

    try {
      String tips = geminiApiClient.getWellBeingTips();
      model.addAttribute("wellBeingTips", tips);
    } catch (Exception e) {
      model.addAttribute("wellBeingTips", "Currently unavailable.");
      log.error("Failed to fetch well-being tips", e);
    }
    return "Home";
  }
}

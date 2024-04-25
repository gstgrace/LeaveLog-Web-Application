package com.lms.models;

import java.time.LocalDate;

/**
 * Interface representing an Employee in the system.
 */
public interface Employee {
  int getId();
  String getFullName();
  String getFirstName();
  String getLastName();
  String getEmail();
  String getRole();
  String getPassword();
  boolean canApproveLeave();

  void setId(int id);
  void setEmail(String email);
  void setFirstName(String firstName);
  void setLastName(String lastName);
   void setPassword(String password);
  void setOnboardingDate(LocalDate onboardingDate);
  void setManager(AbstractEmployee manager);
  void setRole(String role);
}
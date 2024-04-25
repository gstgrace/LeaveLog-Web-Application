package com.lms.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

  @Test
  void testManagerCanApproveLeave() {
    AbstractEmployee manager = new Manager();
    assertTrue(manager.canApproveLeave());
  }

  @Test
  void testNonManagerCannotApproveLeave() {
    AbstractEmployee nonManager = new NonManagerEmployee();
    assertFalse(nonManager.canApproveLeave());
  }

  @Test
  void testFullNameConcatenation() {
    AbstractEmployee employee = new Manager();
    employee.setFirstName("John");
    employee.setLastName("Doe");
    assertEquals("John Doe", employee.getFullName());
  }

  @Test
  void testSetActive() {
    AbstractEmployee employee = new Manager();
    employee.setActive(false);
    assertFalse(employee.isActive());
  }

  @Test
  void testManagerCanHaveManager() {
    Manager manager = new Manager();
    AbstractEmployee subManager = new Manager();
    manager.setManager(subManager);
    assertEquals(subManager, manager.getManager());
  }

  @Test
  void testOnboardingDate() {
    AbstractEmployee employee = new Manager();
    LocalDate date = LocalDate.of(2022, 4, 25);
    employee.setOnboardingDate(date);
    assertEquals(date, employee.getOnboardingDate());
  }
}

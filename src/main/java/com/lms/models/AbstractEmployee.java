package com.lms.models;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;


/**
 * Abstract class representing a general Employee in the system.
 */
@Entity
@Table(name = "userinfo")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractEmployee implements Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "role", nullable = false, insertable = false, updatable = false)
  private String role;

  @Column(name = "password", nullable = false)
  private String password;

  @ManyToOne
  @JoinColumn(name = "manager_id")
  private AbstractEmployee manager;

  @Column(name = "onboarding_date")
  private LocalDate onboardingDate;

  @Column(name = "active", nullable = false)
  private boolean active;

  public int getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public String getRole() {
    return role;
  }

  public String getPassword() {
    return password;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getFirstName() {
    return firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }


  public AbstractEmployee getManager() {
    return manager;
  }

  public void setManager(AbstractEmployee manager) {
    this.manager = manager;
  }

  public LocalDate getOnboardingDate() {
    return onboardingDate;
  }

  public void setOnboardingDate(LocalDate onboardingDate) {
    this.onboardingDate = onboardingDate;
  }

  public abstract boolean canApproveLeave();
}
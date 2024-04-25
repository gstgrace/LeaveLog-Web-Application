package com.lms.models;

import jakarta.persistence.*;

/**
 * Represents an employee's leave balance for a specific type of leave in a given year.
 * This entity is part of a composite primary key setup which includes the employee, leave type, and year.
 */
@Entity
@Table(name = "user_leave_balance")
@IdClass(UserLeaveBalanceId.class)
public class UserLeaveBalance {

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private AbstractEmployee user;

  @Id
  @ManyToOne
  @JoinColumn(name = "leave_type_id", referencedColumnName = "id")
  private LeaveType leaveType;

  @Column(name = "current_balance")
  private Integer currentBalance;

  @Id
  @Column(name = "year")
  private Integer year;

  /**
   * Gets the employee associated with this leave balance.
   * @return the employee
   */
  public Employee getUser() {
    return user;
  }

  /**
   * Sets the employee associated with this leave balance.
   * @param user the employee to set
   */
  public void setUser(Employee user) {
    this.user = (AbstractEmployee) user;
  }

  /**
   * Gets the type of leave associated with this balance.
   * @return the leave type
   */
  public LeaveType getLeaveType() {
    return leaveType;
  }

  /**
   * Sets the type of leave associated with this balance.
   * @param leaveType the leave type to set
   */
  public void setLeaveType(LeaveType leaveType) {
    this.leaveType = leaveType;
  }

  /**
   * Gets the current balance of leaves for the employee.
   * @return the current balance of leaves
   */
  public Integer getCurrentBalance() {
    return currentBalance;
  }

  /**
   * Sets the current balance of leaves for the employee.
   * @param currentBalance the number of leaves to set
   */
  public void setCurrentBalance(Integer currentBalance) {
    this.currentBalance = currentBalance;
  }

  /**
   * Gets the year associated with this leave balance.
   * @return the year
   */
  public Integer getYear() {
    return year;
  }

  /**
   * Sets the year associated with this leave balance.
   * @param year the year to set
   */
  public void setYear(Integer year) {
    this.year = year;
  }
}

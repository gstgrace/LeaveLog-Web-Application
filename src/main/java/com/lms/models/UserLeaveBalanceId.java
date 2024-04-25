package com.lms.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the composite primary key for the UserLeaveBalance entity using user ID,
 * leave type ID, and year. This class is used to ensure uniqueness and integrity of
 * leave balance records within the database.
 */
public class UserLeaveBalanceId implements Serializable {
  private Integer user;    // Corresponds to the user_id
  private Integer leaveType; // Corresponds to the leave_type_id
  private Integer year;    // For year field

  /**
   * Default constructor for JPA.
   */
  public UserLeaveBalanceId() {
  }

  /**
   * Constructs a new composite key for user leave balance.
   *
   * @param user the user ID
   * @param leaveType the leave type ID
   * @param year the applicable year
   */
  public UserLeaveBalanceId(Integer user, Integer leaveType, Integer year) {
    this.user = user;
    this.leaveType = leaveType;
    this.year = year;
  }

  /**
   * Checks if this UserLeaveBalanceId is equal to another instance.
   *
   * @param o the object to compare with
   * @return true if the given object represents the same key, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserLeaveBalanceId that = (UserLeaveBalanceId) o;
    return Objects.equals(user, that.user) &&
        Objects.equals(leaveType, that.leaveType) &&
        Objects.equals(year, that.year);
  }

  /**
   * Generates a hash code for this UserLeaveBalanceId.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(user, leaveType, year);
  }
}


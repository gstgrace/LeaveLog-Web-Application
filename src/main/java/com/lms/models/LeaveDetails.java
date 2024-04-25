package com.lms.models;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the details of an employee's leave, including dates, type, reason, and status.
 */
@Entity
@Table(name = "leave_details")
public class LeaveDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private AbstractEmployee user;

  @NotBlank(message = "Employee name cannot be blank!")
  private String employeeName;

  @NotNull(message = "Please provide a start date!")
  private LocalDate fromDate;

  @NotNull(message = "Please provide an end date!")
  private LocalDate toDate;

  @ManyToOne
  @JoinColumn(name = "leave_type_id", nullable = false)
  private LeaveType leaveType;

  @NotBlank(message = "Please provide a reason for the leave!")
  private String reason;

  private Integer duration; // Nullable if duration is not required

  @Column(name = "status")
  private String status;

  @NotNull(message = "Active status must be specified!")
  private Boolean active;

  // Getters and Setters

  public int getId() { return id; }
  public Employee getUser() { return user; }
  public String getEmployeeName() { return employeeName; }
  public LocalDate getFromDate() { return fromDate; }
  public LocalDate getToDate() { return toDate; }
  public LeaveType getLeaveType() { return leaveType; }
  public String getReason() { return reason; }
  public Integer getDuration() { return duration; }
  public Boolean getActive() { return active; }
  public String getStatus() { return status; }

  public void setId(int id) { this.id = id; }
  public void setUser(Employee user) { this.user = (AbstractEmployee) user; }
  public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
  public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
  public void setToDate(LocalDate toDate) { this.toDate = toDate; }
  public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }
  public void setReason(String reason) { this.reason = reason; }
  public void setDuration(Integer duration) { this.duration = duration; }
  public void setStatus(String status) { this.status = status; }
  public void setActive(Boolean active) { this.active = active; }
}

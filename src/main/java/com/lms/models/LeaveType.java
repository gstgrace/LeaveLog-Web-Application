package com.lms.models;

import jakarta.persistence.*;

/**
 * Represents the different types of leaves available in the LeaveLog system.
 * It includes details about the type of leave, description, and rules regarding
 * annual allocation and maximum accumulation.
 */
@Entity
@Table(name = "leave_types")
public class LeaveType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "type_name", unique = true, nullable = false)
  private String typeName; // Properly annotated field for type name

  @Column(name = "description")
  private String description;

  @Column(name = "annual_allocation")
  private Integer annualAllocation;

  @Column(name = "max_accumulation")
  private Integer maxAccumulation;

  // Getters and Setters

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }
  public String getTypeName() { return typeName; }
  public void setTypeName(String typeName) { this.typeName = typeName; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Integer getAnnualAllocation() { return annualAllocation; }
  public void setAnnualAllocation(Integer annualAllocation) { this.annualAllocation = annualAllocation; }
  public Integer getMaxAccumulation() { return maxAccumulation; }
  public void setMaxAccumulation(Integer maxAccumulation) { this.maxAccumulation = maxAccumulation; }

  /**
   * Provides a string representation of the leave type, primarily using the type name.
   *
   * @return A string representing the name of the leave type.
   */
  @Override
  public String toString() {
    return this.typeName;  // Use the correctly annotated 'typeName' field here
  }
}

package com.lms.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Entity class representing a Non-Manager Employee in the system.
 */
@Entity
@DiscriminatorValue("Employee")
public class NonManagerEmployee extends AbstractEmployee {

  @Override
  public boolean canApproveLeave() {
    return false;
  }
}
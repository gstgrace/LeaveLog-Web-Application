package com.lms.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Entity class representing a Manager in the system.
 */
@Entity
@DiscriminatorValue("Manager")
public class Manager extends AbstractEmployee {

  @Override
  public boolean canApproveLeave() {
    return true;
  }
}

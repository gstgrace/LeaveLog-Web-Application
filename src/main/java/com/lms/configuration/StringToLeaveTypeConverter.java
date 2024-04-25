package com.lms.configuration;

import com.lms.models.LeaveType;
import com.lms.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * This class implements the Converter interface provided by Spring to convert
 * a String object that represents the ID of a LeaveType into a LeaveType object.
 * This conversion is essential for processing dynamic forms where leave types
 * are often submitted as strings that must be converted to actual
 * LeaveType entities for further processing.
 */
@Component
public class StringToLeaveTypeConverter implements Converter<String, LeaveType> {
  private final LeaveTypeRepository leaveTypeRepository;

  /**
   * Constructs a new StringToLeaveTypeConverter with a LeaveTypeRepository.
   * This repository is used to fetch LeaveType entities based on their ID.
   *
   * @param leaveTypeRepository the repository used to access LeaveType entities
   */
  @Autowired
  public StringToLeaveTypeConverter(LeaveTypeRepository leaveTypeRepository) {
    this.leaveTypeRepository = leaveTypeRepository;
  }

  /**
   * Converts the given string (which should represent an integer ID) into a LeaveType
   * entity. If the string is null or empty, it returns null. If the string cannot be
   * parsed into an integer or no LeaveType with the given ID exists, it throws an
   * IllegalArgumentException.
   *
   * @param source the string input to convert
   * @return a LeaveType entity corresponding to the input ID
   * @throws IllegalArgumentException if the input is not a valid integer or no LeaveType
   *         is found for the given ID
   */
  @Override
  public LeaveType convert(String source) {
    if (source == null || source.trim().isEmpty()) {
      return null;  // return null if source is null or empty
    }

    Integer id;
    try {
      id = Integer.valueOf(source);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid leave type ID: " + source, e);
    }

    return leaveTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid leave type: " + source));
  }
}

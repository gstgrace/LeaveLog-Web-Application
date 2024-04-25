package com.lms.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.lms.models.Employee;
import com.lms.models.Manager;
import com.lms.models.NonManagerEmployee;

/**
 * This class implements the Converter interface provided by Spring and is used to convert
 * a String object into an Employee object. It supports converting strings that represent
 * different types of employees into their respective concrete classes.
 */
@Component
public class StringToEmployeeConverter implements Converter<String, Employee> {

  /**
   * Converts a String to a corresponding Employee subtype. This method determines the type
   * of employee to create based on the provided string.
   *
   * @param source the String that describes the type of Employee to be created
   * @return an instance of Employee corresponding to the input string
   * @throws IllegalArgumentException if the input string does not correspond to any known employee type
   */
  @Override
  public Employee convert(String source) {
    if (source == null) {
      return null;
    }
    switch (source) {
      case "Manager":
        return new Manager();
      case "Employee":
        return new NonManagerEmployee();
      default:
        throw new IllegalArgumentException("Invalid employee type: " + source);
    }
  }
}

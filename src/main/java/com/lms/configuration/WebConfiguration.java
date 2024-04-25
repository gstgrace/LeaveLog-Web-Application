package com.lms.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfiguration class configures web-specific settings for the LeaveLog's Spring MVC framework.
 * It implements the WebMvcConfigurer interface, allowing for customization of the formats
 * used in form submissions, particularly by registering custom converters that convert
 * strings into specific object types.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final StringToLeaveTypeConverter stringToLeaveTypeConverter;
  private final StringToEmployeeConverter stringToEmployeeConverter;

  /**
   * Constructs a new WebConfiguration with specified converters.
   *
   * @param stringToLeaveTypeConverter the converter that transforms a String into a LeaveType object
   * @param stringToEmployeeConverter the converter that transforms a String into an Employee object
   */
  @Autowired
  public WebConfiguration(StringToLeaveTypeConverter stringToLeaveTypeConverter, StringToEmployeeConverter stringToEmployeeConverter) {
    this.stringToLeaveTypeConverter = stringToLeaveTypeConverter;
    this.stringToEmployeeConverter = stringToEmployeeConverter;
  }

  /**
   * Registers custom converters with the FormatterRegistry.
   * This method adds custom converters to convert form strings into LeaveType and Employee entities,
   * supporting data binding from form inputs to server-side object models.
   *
   * @param registry the FormatterRegistry to which the converters are added
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(stringToLeaveTypeConverter);
    registry.addConverter(stringToEmployeeConverter);
  }
}

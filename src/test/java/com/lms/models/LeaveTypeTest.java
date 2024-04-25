package com.lms.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveTypeTest {

  @Test
  void testLeaveTypeToString() {
    LeaveType leaveType = new LeaveType();
    leaveType.setTypeName("Vacation");
    assertEquals("Vacation", leaveType.toString());
  }

  @Test
  void testLeaveTypeFields() {
    LeaveType leaveType = new LeaveType();
    leaveType.setTypeName("Sick Leave");
    leaveType.setDescription("Paid sick leave");
    leaveType.setAnnualAllocation(10);
    leaveType.setMaxAccumulation(30);

    assertEquals("Sick Leave", leaveType.getTypeName());
    assertEquals("Paid sick leave", leaveType.getDescription());
    assertEquals(10, leaveType.getAnnualAllocation());
    assertEquals(30, leaveType.getMaxAccumulation());
  }

}

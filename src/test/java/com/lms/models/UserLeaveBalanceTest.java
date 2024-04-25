package com.lms.models;

import com.lms.models.AbstractEmployee;
import com.lms.models.LeaveType;
import com.lms.models.UserLeaveBalance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class UserLeaveBalanceTest {

  @Test
  void testGettersAndSetters() {
    AbstractEmployee user = mock(AbstractEmployee.class);
    LeaveType leaveType = mock(LeaveType.class);

    UserLeaveBalance userLeaveBalance = new UserLeaveBalance();
    userLeaveBalance.setUser(user);
    userLeaveBalance.setLeaveType(leaveType);
    userLeaveBalance.setCurrentBalance(20);
    userLeaveBalance.setYear(2024);

    assertEquals(user, userLeaveBalance.getUser());
    assertEquals(leaveType, userLeaveBalance.getLeaveType());
    assertEquals(20, userLeaveBalance.getCurrentBalance());
    assertEquals(2024, userLeaveBalance.getYear());
  }

}

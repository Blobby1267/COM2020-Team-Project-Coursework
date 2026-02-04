package com.carbon;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({TestUser.class, TestLoginController.class})
public class TestLoginSuite {
}

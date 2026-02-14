package com.carbon;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        TestUser.class,
        TestChallenge.class,
        TestLeaderboard.class,
        TestLoginIntegration.class,
        TestChallengeIntegration.class,
        TestLeaderboardIntegration.class
})
public class TestSuite {
}

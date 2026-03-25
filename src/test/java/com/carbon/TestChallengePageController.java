package com.carbon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.carbon.controller.ChallengePageController;
import com.carbon.model.User;
import com.carbon.repository.UserRepository;
import com.carbon.service.ChallengeService;


@ExtendWith(MockitoExtension.class)
public class TestChallengePageController {

    @Mock
    private ChallengeService challengeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private ChallengePageController challengePageController;

    @Test //Calls the challenge method with no auth and checks the correct Thymeleaf template name is returned
    void TestChallengeReturnsTasksView() {
        
        String viewName = challengePageController.challenge(null, model);

        assertEquals("tasks", viewName);
    }
     @Test  // Calls the method with no authentication, verifies that all challenge frequencies 
           // (Daily, Weekly, Monthly) are fetched from the service and correctly added to the model
    void TestChallengeAddsDailyWeeklyMonthlyChallenges() {
        
        challengePageController.challenge(null, model);

       
        verify(challengeService).getChallengesByFrequency("Daily");
        verify(challengeService).getChallengesByFrequency("Weekly");
        verify(challengeService).getChallengesByFrequency("Monthly");

        verify(model).addAttribute(eq("dailyChallenges"), any());
        verify(model).addAttribute(eq("weeklyChallenges"), any());
        verify(model).addAttribute(eq("monthlyChallenges"), any());

    }
     @Test //Creates a test user with points set, then mocks auth and repo to return the test user. Calls the method with valid authentication and Verifies the user's points were added to the model
    void TestChallengeAddsUserPointsWhenUserLoggedIn() {
        
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPoints(200);

        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);

        challengePageController.challenge(authentication, model);

        verify(model).addAttribute("userPoints", 200);
    }
      @Test  //Calls with null auth to simulate no logged in user and Verifies userPoints is never added to the model
    void TestChallengeDoesNotAddUserPointsWhenAuthIsNull() {
       
        challengePageController.challenge(null, model);

        verify(model, never()).addAttribute(eq("userPoints"), any());
    }
    @Test //Mock auth but return null from the repo to simulate user not existing in the DB, then Calls the method and Verifies userPoints is never added since the user was not found
    void TestChallengeDoesNotAddUserPointsWhenUserNotFound() {
        
        when(authentication.getName()).thenReturn("unknownUser");

        challengePageController.challenge(authentication, model);

        verify(model, never()).addAttribute(eq("userPoints"), any());
    }
        @Test  //Mocks auth to return a valid username, calls the endpoint and check no exception is thrown,  checks 200 OK is returned with the correct body, checks 200 OK is returned with the correct body
    void TestCompleteChallengeSuccessful() {
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> response = challengePageController.completeChallenge(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Challenge completed successfully", response.getBody());

        verify(challengeService, times(1)).completeChallenge("testUser", 1L);
    }
       @Test //Calls the endpoint with null authentication to simulate a non-logged-in user, Checks 401 Unauthorized is returned with the correct error message
    void TestCompleteChallengeNotAuthenticatedReturns401() {
        ResponseEntity<String> response = challengePageController.completeChallenge(1L, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not authenticated", response.getBody());

        verify(challengeService, never()).completeChallenge(any(), any());
    }
        @Test //Mock auth and make the service throw an exception to simulate a bad challenge ID, calls the endpoint with the invalid challenge ID, checks 400 Bad Request is returned with the exception message in the body
    void TestCompleteChallengeServiceThrowsExceptionReturns400() {
        when(authentication.getName()).thenReturn("testUser");
        doThrow(new RuntimeException("Challenge not found"))
            .when(challengeService).completeChallenge("testUser", 99L);

        ResponseEntity<String> response = challengePageController.completeChallenge(99L, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Challenge not found", response.getBody());
    }

    @Test         //Call the redirect endpoint, checks it redirects to the correct /tasks endpoint
    void TestRedirectToTasksReturnsCorrectRedirect() {
        String redirect = challengePageController.redirectToTasks();

        assertEquals("redirect:/tasks", redirect);
    }

}
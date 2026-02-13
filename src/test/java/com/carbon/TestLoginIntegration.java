package com.carbon;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.carbon.repository.UserRepository;
import com.carbon.controller.LoginController;
import com.carbon.model.User;

// import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


@DataJpaTest
public class TestLoginIntegration {

    @Mock
    UserRepository mockRepository;  

    @Mock
    User user;

    String usernameParam = "Davi";
    String passwordParam = "password123";

    @BeforeEach
    void setup(){

    }

    @Test
    void TestHandleLoginSuccess() {
        when(mockRepository.findByUsername("Davi")).thenReturn(user);
        when(user.getPassword()).thenReturn("password123");

        LoginController controller = new LoginController(mockRepository);

        String result = controller.handleLogin(usernameParam, passwordParam);

        Assertions.assertEquals("redirect:/tasks.html",result);

        verify(mockRepository).findByUsername("Davi");
        verify(user).getPassword();
    }

    @Test
    void TestHandleLoginPasswordFail(){
        when(mockRepository.findByUsername("Davi")).thenReturn(user);
        when(user.getPassword()).thenReturn("password");

        LoginController controller = new LoginController(mockRepository);

        String result = controller.handleLogin(usernameParam, passwordParam);

        Assertions.assertEquals("redirect:/login.html?error=true",result);

        verify(mockRepository).findByUsername("Davi");
        verify(user).getPassword();
    }
}

package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.logging.Logger;

@Controller
public class ProfilePageController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @GetMapping("/profile")
    public String Profile(){
        return "profile";
    }

}

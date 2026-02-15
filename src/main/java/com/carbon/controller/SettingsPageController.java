package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.logging.Logger;

@Controller
public class SettingsPageController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @GetMapping("/settings")
    public String Challenge(){
        return "settings";
    }
}

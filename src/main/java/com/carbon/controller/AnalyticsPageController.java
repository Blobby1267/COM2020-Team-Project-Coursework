package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.logging.Logger;

@Controller
public class AnalyticsPageController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @GetMapping("/analytics")
    public String Analytics(){
        return "analytics";
    }

}

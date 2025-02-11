package com.example.crochetPatterns.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OtherControllers {

    @RequestMapping("/main")
    public String returnMainMenu(Model model) {
        return "mainMenu";
    }

}


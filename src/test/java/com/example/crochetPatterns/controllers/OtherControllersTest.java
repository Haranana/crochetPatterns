package com.example.crochetPatterns.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(OtherControllers.class)
@AutoConfigureMockMvc(addFilters = false) //powinno sprawiac, ze spring security nie ingeruej w testy
class OtherControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /main -> widok mainMenu")
    void shouldReturnMainMenu() throws Exception {
        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainMenu"));
    }
}

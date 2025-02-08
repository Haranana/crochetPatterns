package com.example.crochetPatterns.services;

import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// import static org.junit.jupiter.api.Assertions.*;  // do asercji
// import static org.mockito.Mockito.*;             // do metod Mockito

class UserServiceTest {

    // Tutaj np. jest testowany obiekt
    private UserService userService;

    // Mockowane zależności
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @BeforeEach
    void setUp() {
        // otwiera mocki stworzonych przez @Mock
        MockitoAnnotations.openMocks(this);

        // tworzymy nowy obiekt userService, wstrzykując do konstruktora mock
        userService = new UserService(userRepository, userConverter);
    }

    @Test
    void basicTest() {
        // given

        // when

        // then
    }

}

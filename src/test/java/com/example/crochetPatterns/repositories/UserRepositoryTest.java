package com.example.crochetPatterns.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindByEmailDomain() {
        // given

        /* example:
        User user1 = new User();
        user1.setUsername("alice");
        user1.setEmail("alice@google.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("bob");
        user2.setEmail("bob@yahoo.com");
        userRepository.save(user2);

        // when
        List<User> googleUsers = userRepository.findAllByEmailDomain("google.com");

        // then
        // np. asercja
        // assertThat(googleUsers).hasSize(1).extracting(User::getUsername).contains("alice");
        
         */
    }
}

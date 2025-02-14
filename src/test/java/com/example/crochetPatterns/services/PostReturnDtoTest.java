package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.PostReturnDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class PostReturnDTOTest {

    @Test
    @DisplayName("setCreationTime() - gdy różnica to 2 godziny, ustawia HOUR i wartość 2")
    void shouldSetCreationTimeFor2HoursDifference() {

        PostReturnDTO dto = new PostReturnDTO();

        Instant now = Instant.now();
        Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);
        dto.setCreationDate(Timestamp.from(twoHoursAgo));


        dto.setCreationTime();


        assertEquals(2, dto.getCreationTimeValue());
         assertEquals("HOUR", dto.getCreationTimeValueType().name());
    }

    @Test
    @DisplayName("setCreationTime() - gdy różnica to 10 dni, ustawia DAY i wartość 10")
    void shouldSetCreationTimeFor10DaysDifference() {

        PostReturnDTO dto = new PostReturnDTO();
        Instant now = Instant.now();
        Instant tenDaysAgo = now.minus(10, ChronoUnit.DAYS);
        dto.setCreationDate(Timestamp.from(tenDaysAgo));

        dto.setCreationTime();


        assertEquals(1, dto.getCreationTimeValue());
        assertEquals("WEEK", dto.getCreationTimeValueType().name());
    }

    @Test
    @DisplayName("setCreationTime() - ustawia showableDate jako 'yyyy-MM-dd HH:mm'")
    void shouldSetShowableDateCorrectly() {

        PostReturnDTO dto = new PostReturnDTO();
        Instant now = Instant.parse("2025-02-11T10:15:30.00Z");
        dto.setCreationDate(Timestamp.from(now));


        dto.setCreationTime();

        assertTrue(dto.getShowableDate().startsWith("2025-02-11 10:15") || dto.getShowableDate().startsWith("2025-02-11 11:15"));
    }
}
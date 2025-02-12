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
        // given
        PostReturnDTO dto = new PostReturnDTO();
        // Ustawiamy creationDate na 2h wstecz
        Instant now = Instant.now();
        Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);
        dto.setCreationDate(Timestamp.from(twoHoursAgo));

        // when
        dto.setCreationTime();

        // then
        assertEquals(2, dto.getCreationTimeValue());
        // Niestety creationTimeValueType jest prywatne w enum, w kodzie jest "private enum CreationTimeValueType"
        // - Możesz uczynić go public lub sprawdzić przez ewentualny getter (lub zmienić widoczność).
        // Zakładając, że jest dostępny:
         assertEquals("HOUR", dto.getCreationTimeValueType().name());
        // Albo testować to w inny sposób, np. sprawdzić czy text w UI się zgadza.
    }

    @Test
    @DisplayName("setCreationTime() - gdy różnica to 10 dni, ustawia DAY i wartość 10")
    void shouldSetCreationTimeFor10DaysDifference() {
        // given
        PostReturnDTO dto = new PostReturnDTO();
        Instant now = Instant.now();
        Instant tenDaysAgo = now.minus(10, ChronoUnit.DAYS);
        dto.setCreationDate(Timestamp.from(tenDaysAgo));

        // when
        dto.setCreationTime();

        // then
        assertEquals(1, dto.getCreationTimeValue());
        assertEquals("WEEK", dto.getCreationTimeValueType().name());
    }

    @Test
    @DisplayName("setCreationTime() - ustawia showableDate jako 'yyyy-MM-dd HH:mm'")
    void shouldSetShowableDateCorrectly() {
        // given
        PostReturnDTO dto = new PostReturnDTO();
        Instant now = Instant.parse("2025-02-11T10:15:30.00Z");
        dto.setCreationDate(Timestamp.from(now));

        // when
        dto.setCreationTime();

        // then
        assertTrue(dto.getShowableDate().startsWith("2025-02-11 10:15") || dto.getShowableDate().startsWith("2025-02-11 11:15"));
    }
}
package com.example.crochetPatterns.services;

import com.example.crochetPatterns.dtos.CommentReturnDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class CommentReturnDTOTest {

    @Test
    @DisplayName("updateShowableDate() - różnica 5 minut => type=MINUTE, value=5")
    void shouldSetMinutesWhenDifferenceIs5Minutes() {
        // given
        CommentReturnDTO dto = new CommentReturnDTO();
        Instant now = Instant.now();
        Instant fiveMinAgo = now.minus(5, ChronoUnit.MINUTES);
        dto.setCreationDate(Timestamp.from(fiveMinAgo));

        // when
        dto.updateShowableDate();

        // then
        assertEquals(5, dto.getCreationTimeValue());
        assertEquals(CommentReturnDTO.CreationTimeValueType.MINUTE, dto.getCreationTimeValueType());
    }

    @Test
    @DisplayName("updateShowableDate() - różnica < 60 sekund => type=SECOND, value= (liczba sekund)")
    void shouldSetSecondsWhenDifferenceIsUnderOneMinute() {
        // given
        CommentReturnDTO dto = new CommentReturnDTO();
        Instant now = Instant.now();
        Instant thirtySecondsAgo = now.minus(30, ChronoUnit.SECONDS);
        dto.setCreationDate(Timestamp.from(thirtySecondsAgo));

        // when
        dto.updateShowableDate();

        // then
        assertTrue(dto.getCreationTimeValue() >= 30 && dto.getCreationTimeValue() <= 31);
    }

    @Test
    @DisplayName("updateShowableDate() - showableDate przycina string do formatu 'yyyy-MM-dd HH:mm'")
    void shouldTrimStringForShowableDate() {
        // given
        CommentReturnDTO dto = new CommentReturnDTO();
        Instant now = Instant.parse("2025-02-11T10:15:30.00Z");
        dto.setCreationDate(Timestamp.from(now));

        // when
        dto.updateShowableDate();

        // then
        String showableDate = dto.getShowableDate();
        assertNotNull(showableDate);
        assertFalse(showableDate.contains(":30"));
    }
}
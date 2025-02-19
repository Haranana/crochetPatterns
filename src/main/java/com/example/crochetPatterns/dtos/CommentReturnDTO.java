package com.example.crochetPatterns.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReturnDTO {

    @Positive(message = "{number.positive}")
    private Long id;

    @NotEmpty(message = "{comment.empty}")
    @Size(max = 1000, message = "{comment.tooLong}")
    private String text;

    @PastOrPresent(message = "{comment.dateIsFuture}")
    private Timestamp creationDate;

    @NotNull
    @Positive(message = "{number.positive}")
    private Long postId;

    private Long authorId;

    public enum CreationTimeValueType{
        YEAR,
        MONTH,
        WEEK,
        DAY,
        HOUR,
        MINUTE,
        SECOND
    }

    private String showableDate;
    private CreationTimeValueType creationTimeValueType = CreationTimeValueType.HOUR;
    private int creationTimeValue;

    public void updateShowableDate() {

        this.showableDate = creationDate.toString().substring(0, creationDate.toString().lastIndexOf(':'));

        Instant instant1 = creationDate.toInstant();
        Instant instant2 = Instant.now();
        Duration duration = Duration.between(instant1, instant2);

        if (duration.toDays() >= 365) {
            this.creationTimeValueType = CreationTimeValueType.YEAR;
            this.creationTimeValue = (int) (duration.toDays() / 365);
        } else if (duration.toDays() >= 30) {
            this.creationTimeValueType = CreationTimeValueType.MONTH;
            this.creationTimeValue = (int) (duration.toDays() / 30);
        } else if (duration.toDays() >= 7) {
            this.creationTimeValueType = CreationTimeValueType.WEEK;
            this.creationTimeValue = (int) (duration.toDays() / 7);
        } else if (duration.toHours() >= 24) {
            this.creationTimeValueType = CreationTimeValueType.DAY;
            this.creationTimeValue = (int) (duration.toHours() / 24);
        } else if (duration.toMinutes() >= 60) {
            this.creationTimeValueType = CreationTimeValueType.HOUR;
            this.creationTimeValue = (int) (duration.toMinutes() / 60);
        } else if (duration.toMinutes() >= 1) {
            this.creationTimeValueType = CreationTimeValueType.MINUTE;
            this.creationTimeValue = (int) (duration.toMinutes());
        } else {
            this.creationTimeValueType = CreationTimeValueType.SECOND;
            this.creationTimeValue = (int) (duration.toSeconds());
        }
    }
}

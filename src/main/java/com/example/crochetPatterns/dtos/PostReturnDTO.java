package com.example.crochetPatterns.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostReturnDTO {

    private long id;

    @NotEmpty(message = "{post.titleEmpty}")
    @Size(max = 100 , message = "{post.titleTooLong}")
    private String title;

    @Size(max = 10000 , message = "{post.descriptionTooLong}")
    private String description;

    @NotEmpty(message = "{post.emptyURL}")
    private String pdfFilePath;

    @PastOrPresent(message = "{post.dateIsFuture}")
    private Timestamp creationDate;

    @NotNull
    @Positive(message = "{number.positive}")
    private Long authorId;
    private Set<Long> tagIds = new HashSet<>();
    private List<Long> commentIds = new ArrayList<>();

    private String showableDate;
    private int creationTimeValue;
    private CreationTimeValueType  creationTimeValueType = CreationTimeValueType.HOUR;

    private enum CreationTimeValueType{
        YEAR,
        MONTH,
        WEEK,
        DAY,
        HOUR,
        MINUTE,
        SECOND
    }

    public PostReturnDTO(String title, String description, String pdfFilePath, Long authorId) {
        this.title = title;
        this.description = description;
        this.pdfFilePath = pdfFilePath;
        this.authorId = authorId;
    }

    public PostReturnDTO(String title, String description) {
        this.title = title;
        this.description = description;
        this.pdfFilePath = "Lorem Ipsum";
        this.authorId = (long)1;
    }

    public void setCreationTime(){
        showableDate = creationDate.toString().substring(0 , creationDate.toString().lastIndexOf(':'));

        Instant instant1 = creationDate.toInstant();
        Instant instant2 = Instant.now();

        Duration duration = Duration.between(instant1, instant2);

        if(duration.toDays()>=365){
            creationTimeValueType = CreationTimeValueType.YEAR;
            creationTimeValue = (int) (duration.toDays()/365);
        }
        else if(duration.toDays()>=30){
            creationTimeValueType = CreationTimeValueType.MONTH;
            creationTimeValue = (int) (duration.toDays()/30);
        } else if (duration.toDays()>=7) {
            creationTimeValueType = CreationTimeValueType.WEEK;
            creationTimeValue = (int) (duration.toDays() / 7);
        } else if (duration.toHours() >= 24) {
            creationTimeValueType = CreationTimeValueType.DAY;
            creationTimeValue = (int) (duration.toHours() / 24);
        } else if(duration.toMinutes() >= 60){
            creationTimeValueType = CreationTimeValueType.HOUR;
            creationTimeValue = (int) (duration.toMinutes() / 60);
        } else if(duration.toMinutes() >= 1){
            creationTimeValueType = CreationTimeValueType.MINUTE;
            creationTimeValue = (int) (duration.toMinutes());
        } else{
            creationTimeValueType = CreationTimeValueType.SECOND;
            creationTimeValue = (int) (duration.toSeconds());
        }
    }
}

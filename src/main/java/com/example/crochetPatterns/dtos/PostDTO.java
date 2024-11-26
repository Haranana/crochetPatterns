package com.example.crochetPatterns.dtos;

import lombok.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private long id;
    private String title;
    private String description;
    private Timestamp creationDate;
    private String pdfFile;
    private String showableDate;
    private int creationTimeValue;
    private CreationTimeValueType  creationTimeValueType = CreationTimeValueType.HOUR;

    public enum CreationTimeValueType{
        YEAR,
        MONTH,
        WEEK,
        DAY,
        HOUR,
        MINUTE,
        SECOND
    }

    public PostDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setCreationTime(){
        showableDate = creationDate.toString().substring(0 , creationDate.toString().lastIndexOf(':'));

        Instant instant1 = creationDate.toInstant();
        Instant instant2 = Instant.now();

        Duration duration = Duration.between(instant1, instant2);

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();

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

package com.example.crochetPatterns.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "additional_info")
@Setter
@Getter
@NoArgsConstructor
@ToString()
@AllArgsConstructor
public class AdditionalInfo {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}

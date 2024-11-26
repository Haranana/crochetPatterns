package com.example.crochetPatterns.repositories;

import com.example.crochetPatterns.entities.AdditionalInfo;
import com.example.crochetPatterns.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalInfoRepository extends JpaRepository<AdditionalInfo, Long> {
}

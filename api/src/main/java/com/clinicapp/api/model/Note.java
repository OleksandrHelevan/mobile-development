package com.clinicapp.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notes")
public class Note {

    @Id
    private String id;
    private String title;
    private String content;
    private int priority;
    private String category;

    @JsonProperty("isFavorite")
    private Boolean isFavorite;

    private int estimatedTime;
    private String sourceUrl;
}
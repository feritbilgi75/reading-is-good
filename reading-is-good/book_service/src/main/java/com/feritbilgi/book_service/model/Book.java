package com.feritbilgi.book_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "books")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private BigDecimal price;
    @LastModifiedDate 
    private Instant updateAt;
}

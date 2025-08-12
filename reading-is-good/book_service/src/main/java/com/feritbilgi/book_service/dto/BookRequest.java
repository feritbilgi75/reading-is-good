package com.feritbilgi.book_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest { // structure of post request
    private String title;        // Name of book
    private String author;       // Author of Book
    private BigDecimal price;    // Price of Book
}

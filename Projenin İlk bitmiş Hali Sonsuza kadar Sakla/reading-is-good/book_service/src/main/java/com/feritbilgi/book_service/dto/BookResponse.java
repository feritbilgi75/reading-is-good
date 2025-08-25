package com.feritbilgi.book_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse { //This one create endpoint to retrieve all the products.
    private String id;
    private String title;        // Name of book
    private String author;       // Author of Book
    private BigDecimal price;    // Price of Book
}

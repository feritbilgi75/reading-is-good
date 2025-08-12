package com.feritbilgi.book_service.controller;


import com.feritbilgi.book_service.dto.BookRequest;
import com.feritbilgi.book_service.dto.BookResponse;
import com.feritbilgi.book_service.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService; // Post request is being processed here, so we will use this class'es methods as you can see in createBook method.

    @PostMapping  // we'll do post request,so we added it.
    @ResponseStatus(HttpStatus.CREATED)
    public void createBook(@RequestBody BookRequest bookRequest){  //it will create post request
        bookService.createBook(bookRequest); // This will create endpoint to create product.
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponse> getAllBooks(){   //This one create endpoint to retrieve all the products.
        return bookService.getAllBooks();
    }


}

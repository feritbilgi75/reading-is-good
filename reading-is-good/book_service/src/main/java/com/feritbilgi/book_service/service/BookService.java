package com.feritbilgi.book_service.service;

import com.feritbilgi.book_service.dto.BookRequest;
import com.feritbilgi.book_service.dto.BookResponse;
import com.feritbilgi.book_service.model.Book;
import com.feritbilgi.book_service.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j //for logs
public class BookService {

    //Proccessing of request (post request). That's why we use Controller to make an order and use Service as machine to process datas.

    private final BookRepository bookRepository; // You should save the book, so that you should call (or construct) the repo.

    /*
    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }
    //Bunun yerine @RequiredArgsConstructor kulanıyoruz.
*/


    public void createBook(BookRequest bookRequest){
        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .price(bookRequest.getPrice())
                .build();

        //When you created it, you should save it.
        bookRepository.save(book);  // Save method comes thanks to inheritance

        log.info("Book {} is saved", book.getId());
    }

    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(this::mapToBookResponse).toList(); //After we mapped it, we need to collect them into a list.
        // We return that list into the controller
    }

    private BookResponse mapToBookResponse(Book book){
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .build();

    }
}

package com.feritbilgi.book_service.repository;

import com.feritbilgi.book_service.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> { // We used string because it's the type of ID
}

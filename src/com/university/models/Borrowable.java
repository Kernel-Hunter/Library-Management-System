package com.university.models;

import com.university.exceptions.BookNotFoundException;
import com.university.exceptions.BorrowLimitExceededException;
import com.university.exceptions.LateReturnException;

public interface Borrowable {
    
    void borrow(Student student) throws BorrowLimitExceededException, BookNotFoundException;
    void returnBook() throws LateReturnException;
}
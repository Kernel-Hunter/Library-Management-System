package com.university.exceptions;

public class BorrowLimitExceededException extends RuntimeException {
    
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
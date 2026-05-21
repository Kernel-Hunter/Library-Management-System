package com.university.models;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import com.university.exceptions.BookNotFoundException;
import com.university.exceptions.BorrowLimitExceededException;
import com.university.exceptions.LateReturnException;

public class Book extends LibraryItem implements Borrowable, Serializable {

    private static final long serialVersionUID = 1L;
    private static final int MAX_BORROW_HOURS = 3;

    private String isbn;
    private String author;
    private Student borrowedBy;
    private LocalDateTime borrowTime;

    public Book(String itemId, String isbn, String title, String author) {
        super(itemId, title);
        this.isbn = isbn;
        this.author = author;
    }

    @Override
    public void borrow(Student student)
            throws BorrowLimitExceededException, BookNotFoundException {

        if (!isAvailable) {
            throw new BookNotFoundException(
                "Book '" + title + "' is not available.");
        }
        if (student.hasBorrowedBook()) {
            throw new BorrowLimitExceededException(
                "Student " + student.getName() + " already has a borrowed book.");
        }

        this.borrowedBy = student;
        this.borrowTime = LocalDateTime.now();
        this.isAvailable = false;
        student.setCurrentBook(this);
        student.logEvent("Borrowed: " + title);
    }

    @Override
    public void returnBook() throws LateReturnException {
        if (borrowTime != null) {
            long minutesBorrowed = ChronoUnit.MINUTES.between(
                borrowTime, LocalDateTime.now());
            if (minutesBorrowed > MAX_BORROW_HOURS * 60) {
                long minutesLate = minutesBorrowed - (MAX_BORROW_HOURS * 60);
                throw new LateReturnException(
                    "Book returned " + minutesLate + " minutes late!", minutesLate);
            }
        }
        if (borrowedBy != null) {
            borrowedBy.logEvent("Returned: " + title);
            borrowedBy.setCurrentBook(null);
        }
        this.borrowedBy = null;
        this.borrowTime = null;
        this.isAvailable = true;
    }

    @Override
    public String getDetails() {
        return "Book[" + itemId + "] '" + title + "' by " + author
            + " | " + (isAvailable ? "Available" : "Borrowed by " + borrowedBy.getName());
    }

    // Persistable methods (from LibraryItem)
    @Override
    public void saveToFile(String filePath) throws IOException {
        // implemented in FileLogger
    }

    @Override
    public void loadFromFile(String filePath) throws IOException {
        // implemented in FileLogger
    }

    // Getters and Setters
    public String getIsbn()              { return isbn; }
    public String getAuthor()            { return author; }
    public Student getBorrowedBy()       { return borrowedBy; }
    public LocalDateTime getBorrowTime() { return borrowTime; }
    public void setBorrowedBy(Student s) { this.borrowedBy = s; }

    @Override
    public String toString() { return getDetails(); }
}
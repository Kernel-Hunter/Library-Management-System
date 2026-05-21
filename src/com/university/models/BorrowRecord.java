package com.university.models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class BorrowRecord {

    private String recordId;
    private Student student;
    private Book book;
    private LocalDateTime borrowTime;
    private LocalDateTime returnTime;
    private boolean isLate;

    public BorrowRecord(Student student, Book book) {
        this.recordId  = UUID.randomUUID().toString();
        this.student   = student;
        this.book      = book;
        this.borrowTime = LocalDateTime.now();
        this.isLate    = false;
    }

    public boolean isActive() {
        return returnTime == null;
    }

    public boolean isLate() {
        if (borrowTime == null) return false;
        long minutes = ChronoUnit.MINUTES.between(borrowTime, LocalDateTime.now());
        return minutes > 180; // 3 hours
    }

    public long getDurationMinutes() {
        LocalDateTime end = (returnTime != null) ? returnTime : LocalDateTime.now();
        return ChronoUnit.MINUTES.between(borrowTime, end);
    }

    public void closeRecord() {
        this.returnTime = LocalDateTime.now();
        this.isLate = isLate();
    }

    // Getters
    public String getRecordId()          { return recordId; }
    public Student getStudent()          { return student; }
    public Book getBook()                { return book; }
    public LocalDateTime getBorrowTime() { return borrowTime; }
    public LocalDateTime getReturnTime() { return returnTime; }

    @Override
    public String toString() {
        return "Record[" + recordId.substring(0, 8) + "] "
            + student.getName() + " → " + book.getTitle()
            + " | " + (isActive() ? "Active" : "Returned")
            + " | Late: " + isLate;
    }
}
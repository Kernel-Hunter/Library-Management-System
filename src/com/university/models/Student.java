package com.university.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.university.util.Notifiable;

public class Student implements Notifiable, Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private String name;
    private String email;
    private Book currentBook;
    private List<String> borrowHistory;

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.borrowHistory = new ArrayList<>();
    }

    public boolean hasBorrowedBook() {
        return currentBook != null;
    }

    @Override
    public void sendAlert(String message) {
        System.out.println("[ALERT for " + name + "] " + message);
    }

    @Override
    public void logEvent(String event) {
        borrowHistory.add(event);
        System.out.println("[LOG] " + event);
    }

    // Getters and Setters
    public String getStudentId()          { return studentId; }
    public String getName()               { return name; }
    public String getEmail()              { return email; }
    public Book getCurrentBook()          { return currentBook; }
    public List<String> getBorrowHistory(){ return borrowHistory; }

    public void setCurrentBook(Book book) {
        this.currentBook = book;
    }

    @Override
    public String toString() {
        return "Student[" + studentId + "] " + name;
    }
}
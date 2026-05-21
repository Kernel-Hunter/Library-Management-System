package com.university.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class BoxBooking {

    private String    bookingId;
    private Student   student;
    private Box       box;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String    status;

    public BoxBooking(Student student, Box box,
                      LocalDate bookingDate,
                      LocalTime startTime,
                      LocalTime endTime) {
        this.bookingId   = UUID.randomUUID().toString();
        this.student     = student;
        this.box         = box;
        this.bookingDate = bookingDate;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.status      = "ACTIVE";
    }

    // Duration in minutes
    public long getDurationMinutes() {
        return ChronoUnit.MINUTES.between(startTime, endTime);
    }

    public void cancel() { this.status = "CANCELLED"; }

    // Getters
    public String    getBookingId()   { return bookingId; }
    public Student   getStudent()     { return student; }
    public Box       getBox()         { return box; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalTime getStartTime()   { return startTime; }
    public LocalTime getEndTime()     { return endTime; }
    public String    getStatus()      { return status; }

    @Override
    public String toString() {
        return "Booking[" + bookingId.substring(0, 8) + "] "
             + student.getName() + " | Box: " + box.getBoxId()
             + " | " + startTime + " - " + endTime
             + " | " + status;
    }
}
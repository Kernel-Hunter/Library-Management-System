package com.university.exceptions;

public class BookingLimitExceededException extends Exception {
    
    private long hoursAlreadyBooked;
    
    public BookingLimitExceededException(String message, long hoursAlreadyBooked) {
        super(message);
        this.hoursAlreadyBooked = hoursAlreadyBooked;
    }
    
    public long getHoursAlreadyBooked() {
        return hoursAlreadyBooked;
    }
}
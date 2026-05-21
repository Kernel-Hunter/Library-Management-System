package com.university.exceptions;

public class LateReturnException extends Exception {
    
    private long minutesLate;
    
    public LateReturnException(String message, long minutesLate) {
        super(message);
        this.minutesLate = minutesLate;
    }
    
    public long getMinutesLate() {
        return minutesLate;
    }
}
package com.university.exceptions;

public class BoxNotAvailableException extends Exception {
    
    private String boxId;
    
    public BoxNotAvailableException(String message, String boxId) {
        super(message);
        this.boxId = boxId;
    }
    
    public String getBoxId() {
        return boxId;
    }
}
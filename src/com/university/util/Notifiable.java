package com.university.util;

public interface Notifiable {
    
    void sendAlert(String message);
    void logEvent(String event);
}
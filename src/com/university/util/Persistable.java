package com.university.util;

import java.io.IOException;

public interface Persistable {
    
    void saveToFile(String filePath) throws IOException;
    void loadFromFile(String filePath) throws IOException;
}
package com.university.manager;

import java.sql.*;
import java.util.*;
import com.university.db.DatabaseConnector;
import com.university.exceptions.*;
import com.university.models.*;
import com.university.util.*;
import java.util.Collection;
public class LibraryManager {

    private Map<String, Book>    books    = new HashMap<>();
    private Map<String, Student> students = new HashMap<>();
    private List<BorrowRecord>   records  = new ArrayList<>();
    private Repository<Book>     bookRepo = new Repository<>();
    private FileLogger           logger;

    public LibraryManager() {
        this.logger = new FileLogger("library.log");
        loadFromDatabase();
    }

    // Load all students and books from database
    private void loadFromDatabase() {
        try (Connection conn = DatabaseConnector.getConnection()) {

            // Load students
            ResultSet rs = conn.createStatement()
                               .executeQuery("SELECT * FROM Students");
            while (rs.next()) {
                Student s = new Student(
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
                students.put(s.getStudentId(), s);
            }

            // Load books
            rs = conn.createStatement()
                     .executeQuery("SELECT * FROM Books");
            while (rs.next()) {
                Book b = new Book(
                    rs.getString("item_id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author")
                );
                b.setAvailable(rs.getBoolean("is_available"));
                books.put(b.getItemId(), b);
                bookRepo.add(b);
            }

            System.out.println("Loaded " + students.size() + " students and "
                             + books.size() + " books from database.");

        } catch (SQLException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    // Borrow a book
    public void borrowBook(String studentId, String bookId) {
        Student student = students.get(studentId);
        Book book       = books.get(bookId);

        if (student == null) {
            System.out.println("Student not found: " + studentId);
            return;
        }
        if (book == null) {
            System.out.println("Book not found: " + bookId);
            return;
        }

        try {
            book.borrow(student);
            BorrowRecord record = new BorrowRecord(student, book);
            records.add(record);
            saveRecordToDatabase(record);
            logger.logEvent("BORROW | " + studentId + " | " + bookId);
            System.out.println("Success! " + student.getName()
                             + " borrowed '" + book.getTitle() + "'");

        } catch (BorrowLimitExceededException e) {
            System.out.println("Cannot borrow: " + e.getMessage());
            logger.logEvent("REJECTED | " + studentId + " | already has a book");

        } catch (BookNotFoundException e) {
            System.out.println("Cannot borrow: " + e.getMessage());
            logger.logEvent("REJECTED | " + bookId + " | not available");
        }
    }

    // Return a book
    public void returnBook(String studentId, String bookId) {
        Student student = students.get(studentId);
        Book book       = books.get(bookId);

        if (student == null || book == null) {
            System.out.println("Invalid student or book ID.");
            return;
        }

        try {
            book.returnBook();
            updateReturnInDatabase(studentId, bookId);
            logger.logEvent("RETURN | " + studentId + " | " + bookId + " | ON TIME");
            System.out.println("Book returned successfully!");

        } catch (LateReturnException e) {
            // Still complete the return but flag as late
            book.setAvailable(true);
            book.setBorrowedBy(null);
            student.setCurrentBook(null);
            updateReturnInDatabase(studentId, bookId);
            logger.logEvent("LATE | " + studentId + " | " + e.getMinutesLate() + " min late");
            System.out.println("Book returned BUT " + e.getMessage());
        }
    }

    // Show all books
    public void showAllBooks() {
        System.out.println("\n=== All Books ===");
        for (Book b : books.values()) {
            System.out.println(b.getDetails());
        }
    }

    // Show all students
    public void showAllStudents() {
        System.out.println("\n=== All Students ===");
        for (Student s : students.values()) {
            System.out.println(s + " | Has book: " + s.hasBorrowedBook());
        }
    }

    // Show available books only
    public void showAvailableBooks() {
        System.out.println("\n=== Available Books ===");
        List<Book> available = bookRepo.findAll(b -> b.isAvailable());
        if (available.isEmpty()) {
            System.out.println("No books available right now.");
        } else {
            for (Book b : available) {
                System.out.println(b.getDetails());
            }
        }
    }

    // Check for late returns
    public void checkLateReturns() {
        System.out.println("\n=== Checking Late Returns ===");
        boolean found = false;
        for (BorrowRecord r : records) {
            if (r.isActive() && r.isLate()) {
                System.out.println("OVERDUE: " + r);
                found = true;
            }
        }
        if (!found) System.out.println("No late returns.");
    }

    // Save borrow record to database
    private void saveRecordToDatabase(BorrowRecord record) {
        String sql = "INSERT INTO BorrowRecords "
                   + "(record_id, student_id, book_id, borrow_time) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.getRecordId());
            ps.setString(2, record.getStudent().getStudentId());
            ps.setString(3, record.getBook().getItemId());
            ps.setTimestamp(4, Timestamp.valueOf(record.getBorrowTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // Update return in database
    private void updateReturnInDatabase(String studentId, String bookId) {
        String sql = "UPDATE BorrowRecords SET return_time = ? "
                   + "WHERE student_id = ? AND book_id = ? "
                   + "AND return_time IS NULL";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(2, studentId);
            ps.setString(3, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }
    public Collection<com.university.models.Book> getAllBooks() {
        return books.values();
    }

    public Collection<com.university.models.Student> getAllStudents() {
        return students.values();
    }
    public Student getStudentById(String studentId) {
        return students.get(studentId);
    }
}
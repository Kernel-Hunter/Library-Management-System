package com.university;

import java.util.Scanner;
import com.university.manager.LibraryManager;

public class Main {

    public static void main(String[] args) {

        LibraryManager manager = new LibraryManager();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        System.out.println("==========================================");
        System.out.println("   Welcome to SMU Library System");
        System.out.println("==========================================");

        while (choice != 6) {
            System.out.println("\n---------- MAIN MENU ----------");
            System.out.println("1. Show all books");
            System.out.println("2. Show available books");
            System.out.println("3. Show all students");
            System.out.println("4. Borrow a book");
            System.out.println("5. Return a book");
            System.out.println("6. Exit");
            System.out.println("-------------------------------");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1:
                    manager.showAllBooks();
                    break;
                case 2:
                    manager.showAvailableBooks();
                    break;
                case 3:
                    manager.showAllStudents();
                    break;
                case 4:
                    System.out.print("Enter Student ID (e.g. S001): ");
                    String studentId = scanner.nextLine().trim();
                    System.out.print("Enter Book ID (e.g. B001): ");
                    String bookId = scanner.nextLine().trim();
                    manager.borrowBook(studentId, bookId);
                    break;
                case 5:
                    System.out.print("Enter Student ID: ");
                    String sId = scanner.nextLine().trim();
                    System.out.print("Enter Book ID: ");
                    String bId = scanner.nextLine().trim();
                    manager.returnBook(sId, bId);
                    break;
                case 6:
                    System.out.println("\nGoodbye! Library system closed.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 6.");
            }
        }
        scanner.close();
    }
}
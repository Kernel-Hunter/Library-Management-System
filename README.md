# Library Management System (Java • Swing GUI • MySQL)

A Java-based **Library Management System** with both a **Swing desktop interface** and a **command-line interface (CLI)**.  
It connects to a **MySQL database** to manage **students**, **books**, and **borrow/return transactions**, and also includes a module to **book study boxes** (rooms/desks) with time slots and daily limits.

---

## Features

### Books & Borrowing
- View **all books** and **available books**
- Borrow and return books
- Borrowing rules enforced with custom exceptions (e.g., borrow limits, unavailable book)
- Late return handling (returns still complete, but are flagged as late)

### Students
- Load and display students from the database
- Lookup students by ID

### Study Boxes (Booking)
- View available **study boxes**
- Book a box by time slot (start/end time)
- Cancel bookings
- Check remaining allowed booking time for the day
- Availability and daily booking-limit rules enforced with exceptions

### Logging
- Writes actions to `library.log` (borrow/return/late/rejected events)

---

## Project Structure

- `src/com/university/Main.java`  
  CLI entry point (menu: show books/students, borrow, return)

- `src/com/university/LibraryGUI.java`  
  Swing desktop UI (tabs for **Books** and **Study Boxes**)

- `src/com/university/manager/LibraryManager.java`  
  Core logic for loading data, borrowing/returning, and DB updates

- `src/com/university/manager/BoxManager.java`  
  Logic for study-box availability and bookings

- `src/com/university/db/DatabaseConnector.java`  
  MySQL connection helper (JDBC)

---

## Requirements

- **Java** (JDK 8+ recommended)
- **MySQL**
- **MySQL JDBC driver** on your classpath (Connector/J)

> Note: This repository currently does not include Maven/Gradle config, so you’ll need to run it from an IDE (IntelliJ/Eclipse) or set the classpath manually.

---

## Database Setup (MySQL)

The app expects a database named:

- `library_db`

And at least these tables (based on the SQL used in code):

- `Students` (must include: `student_id`, `name`, `email`)
- `Books` (must include: `item_id`, `isbn`, `title`, `author`, `is_available`)
- `BorrowRecords` (must include: `record_id`, `student_id`, `book_id`, `borrow_time`, `return_time`)

### Configure connection credentials

Update the credentials in:

- `src/com/university/db/DatabaseConnector.java`

It currently uses:

- URL: `jdbc:mysql://localhost:3306/library_db`
- USER: `root`

**Important:** do not commit real passwords. Use environment variables or a local config approach if you plan to share the repo publicly.

---

## Run the Application

### Option A - Run the Swing GUI
Run:
- `src/com/university/LibraryGUI.java` (contains its own `main`)

### Option B - Run the CLI
Run:
- `src/com/university/Main.java`

CLI menu includes:
1. Show all books  
2. Show available books  
3. Show all students  
4. Borrow a book  
5. Return a book  
6. Exit  

---

## Notes / Known Improvements
- Add a **Maven/Gradle** build file for easier setup
- Move DB credentials out of source code (use `.env`, config file, or secrets)
- Add SQL scripts in a `/db` folder to create tables and seed sample data
- Add automated tests for borrowing/booking rules

---

## License
MIT — see [LICENSE](LICENSE).

## Author
Karim Masmoudi  
GitHub: [@Kernel-Hunter](https://github.com/Kernel-Hunter)

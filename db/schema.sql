-- Library Management System schema
-- Database: library_db
--
-- This schema matches the SQL queries used in:
-- - src/com/university/manager/LibraryManager.java
-- - src/com/university/manager/BoxManager.java
--
-- MySQL 8+ recommended.

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- -----------------------------
-- Students
-- -----------------------------
CREATE TABLE IF NOT EXISTS Students (
  student_id VARCHAR(20)  NOT NULL,
  name       VARCHAR(100) NOT NULL,
  email      VARCHAR(120) NOT NULL,
  PRIMARY KEY (student_id),
  UNIQUE KEY uq_students_email (email)
);

-- -----------------------------
-- Books
-- -----------------------------
CREATE TABLE IF NOT EXISTS Books (
  item_id      VARCHAR(20)  NOT NULL,
  isbn         VARCHAR(32)  NOT NULL,
  title        VARCHAR(255) NOT NULL,
  author       VARCHAR(120) NOT NULL,
  is_available BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (item_id),
  KEY idx_books_title  (title),
  KEY idx_books_author (author)
);

-- -----------------------------
-- BorrowRecords
-- -----------------------------
CREATE TABLE IF NOT EXISTS BorrowRecords (
  record_id   VARCHAR(64) NOT NULL,
  student_id  VARCHAR(20) NOT NULL,
  book_id     VARCHAR(20) NOT NULL,
  borrow_time DATETIME    NOT NULL,
  return_time DATETIME    NULL,
  PRIMARY KEY (record_id),
  KEY idx_borrowrecords_student (student_id),
  KEY idx_borrowrecords_book    (book_id),
  CONSTRAINT fk_borrowrecords_student
    FOREIGN KEY (student_id) REFERENCES Students(student_id)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_borrowrecords_book
    FOREIGN KEY (book_id) REFERENCES Books(item_id)
      ON UPDATE CASCADE ON DELETE RESTRICT
);

-- -----------------------------
-- Boxes
-- -----------------------------
CREATE TABLE IF NOT EXISTS Boxes (
  box_id       VARCHAR(20)  NOT NULL,
  bloc         VARCHAR(50)  NOT NULL,
  floor        VARCHAR(50)  NOT NULL,
  is_available BOOLEAN      NOT NULL DEFAULT TRUE,
  PRIMARY KEY (box_id)
);

-- -----------------------------
-- BoxBookings
-- -----------------------------
CREATE TABLE IF NOT EXISTS BoxBookings (
  booking_id   VARCHAR(64) NOT NULL,
  student_id   VARCHAR(20) NOT NULL,
  box_id       VARCHAR(20) NOT NULL,
  booking_date DATE        NOT NULL,
  start_time   TIME        NOT NULL,
  end_time     TIME        NOT NULL,
  status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (booking_id),
  KEY idx_boxbookings_box_date (box_id, booking_date),
  KEY idx_boxbookings_student_date (student_id, booking_date),
  CONSTRAINT fk_boxbookings_student
    FOREIGN KEY (student_id) REFERENCES Students(student_id)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_boxbookings_box
    FOREIGN KEY (box_id) REFERENCES Boxes(box_id)
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_boxbookings_status
    CHECK (status IN ('ACTIVE', 'CANCELLED'))
);

-- Helpful for the "overlap" query used by BoxManager.isBoxFree():
-- start_time < endTime AND end_time > startTime
-- (Index above helps narrow by box/date/status).


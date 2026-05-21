package com.university.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.university.db.DatabaseConnector;
import com.university.exceptions.BookingLimitExceededException;
import com.university.exceptions.BoxNotAvailableException;
import com.university.models.Box;
import com.university.models.BoxBooking;
import com.university.models.Student;
import com.university.util.FileLogger;

public class BoxManager {

    private Map<String, Box>  boxes    = new HashMap<>();
    private List<BoxBooking>  bookings = new ArrayList<>();
    private FileLogger        logger;

    public BoxManager() {
        this.logger = new FileLogger("box_bookings.log");
        loadBoxesFromDatabase();
    }

    private void loadBoxesFromDatabase() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            ResultSet rs = conn.createStatement()
                               .executeQuery("SELECT * FROM Boxes");
            while (rs.next()) {
                Box box = new Box(
                    rs.getString("box_id"),
                    rs.getString("bloc"),
                    rs.getString("floor"),
                    rs.getBoolean("is_available")
                );
                boxes.put(box.getBoxId(), box);
            }
            System.out.println("Loaded " + boxes.size() + " boxes.");
        } catch (SQLException e) {
            System.out.println("Error loading boxes: " + e.getMessage());
        }
    }

    public BoxBooking bookBox(Student student, String boxId,
                              LocalTime startTime, LocalTime endTime)
            throws BoxNotAvailableException, BookingLimitExceededException {

        Box box = boxes.get(boxId);
        if (box == null) {
            throw new BoxNotAvailableException(
                "Box not found: " + boxId, boxId);
        }

        LocalDate today = LocalDate.now();

        if (!isBoxFree(boxId, today, startTime, endTime)) {
            throw new BoxNotAvailableException(
                "Box " + boxId + " is already booked during that time.", boxId);
        }

        long requestedMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long usedMinutes      = getStudentUsedMinutesToday(
                                    student.getStudentId(), today);
        long remainingMinutes = 180 - usedMinutes;

        if (requestedMinutes > remainingMinutes) {
            throw new BookingLimitExceededException(
                "You only have " + remainingMinutes
                + " minutes left today. Requested: "
                + requestedMinutes + " min.",
                usedMinutes / 60);
        }

        BoxBooking booking = new BoxBooking(
            student, box, today, startTime, endTime);
        bookings.add(booking);
        saveBookingToDatabase(booking);
        logger.logEvent("BOOKED | " + student.getStudentId()
                      + " | " + boxId
                      + " | " + startTime + "-" + endTime);
        System.out.println("Box booked successfully! " + booking);
        return booking;
    }

    public void cancelBooking(String bookingId) {
        for (BoxBooking b : bookings) {
            if (b.getBookingId().startsWith(bookingId)) {
                b.cancel();
                cancelInDatabase(b.getBookingId());
                logger.logEvent("CANCELLED | " + bookingId);
                System.out.println("Booking cancelled.");
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    private boolean isBoxFree(String boxId, LocalDate date,
                               LocalTime start, LocalTime end) {
        String sql = "SELECT COUNT(*) FROM BoxBookings "
                   + "WHERE box_id = ? AND booking_date = ? "
                   + "AND status = 'ACTIVE' "
                   + "AND start_time < ? AND end_time > ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, boxId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setTime(3, Time.valueOf(end));
            ps.setTime(4, Time.valueOf(start));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
        return true;
    }

    private long getStudentUsedMinutesToday(String studentId, LocalDate date) {
        String sql = "SELECT SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)) "
                   + "FROM BoxBookings "
                   + "WHERE student_id = ? AND booking_date = ? "
                   + "AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
        return 0;
    }

    private void saveBookingToDatabase(BoxBooking booking) {
        String sql = "INSERT INTO BoxBookings "
                   + "(booking_id, student_id, box_id, "
                   + "booking_date, start_time, end_time, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, booking.getBookingId());
            ps.setString(2, booking.getStudent().getStudentId());
            ps.setString(3, booking.getBox().getBoxId());
            ps.setDate(4, java.sql.Date.valueOf(booking.getBookingDate()));
            ps.setTime(5, Time.valueOf(booking.getStartTime()));
            ps.setTime(6, Time.valueOf(booking.getEndTime()));
            ps.setString(7, booking.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    private void cancelInDatabase(String bookingId) {
        String sql = "UPDATE BoxBookings SET status = 'CANCELLED' "
                   + "WHERE booking_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    public Collection<Box> getAllBoxes()    { return boxes.values(); }
    public List<BoxBooking> getAllBookings() { return bookings; }

    public long getRemainingMinutesToday(String studentId) {
        return 180 - getStudentUsedMinutesToday(
            studentId, LocalDate.now());
    }
}
package com.university;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.Collection;
import com.university.manager.LibraryManager;
import com.university.manager.BoxManager;
import com.university.models.Box;
import com.university.models.BoxBooking;
import com.university.models.Student;

public class LibraryGUI {

    private LibraryManager manager;
    private BoxManager     boxManager;
    private JFrame         frame;

    // Books tab
    private JTable            bookTable;
    private DefaultTableModel bookTableModel;

    // Boxes tab
    private JTable            boxTable;
    private DefaultTableModel boxTableModel;
    private JTable            bookingTable;
    private DefaultTableModel bookingTableModel;

    // Status bars
    private JLabel bookStatusLabel;
    private JLabel boxStatusLabel;

    public LibraryGUI() {
        manager    = new LibraryManager();
        boxManager = new BoxManager();
        buildUI();
    }

    private void buildUI() {
        frame = new JFrame("SMU Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        // ── TOP PANEL ──
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(30, 60, 114));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel titleLabel = new JLabel("SMU Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        // ── TABS ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 14));
        tabs.addTab("  Books  ",  buildBooksTab());
        tabs.addTab("  Study Boxes  ", buildBoxesTab());

        // ── ASSEMBLE ──
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(tabs,     BorderLayout.CENTER);

        refreshBookTable();
        refreshBoxTable();
        frame.setVisible(true);
    }

    // ════════════════════════════════════════
    //  BOOKS TAB
    // ════════════════════════════════════════
    private JPanel buildBooksTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] cols = {"Book ID", "Title", "Author", "Status"};
        bookTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookTable = new JTable(bookTableModel);
        styleTable(bookTable);
        JScrollPane scroll = new JScrollPane(bookTable);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(240, 240, 240));
        JButton btnRefresh  = createButton("Refresh",        new Color(30, 60, 114));
        JButton btnBorrow   = createButton("Borrow Book",    new Color(0, 128, 0));
        JButton btnReturn   = createButton("Return Book",    new Color(180, 60, 0));
        JButton btnStudents = createButton("Show Students",  new Color(100, 0, 130));
        btnPanel.add(btnRefresh);
        btnPanel.add(btnBorrow);
        btnPanel.add(btnReturn);
        btnPanel.add(btnStudents);

        // Status
        bookStatusLabel = createStatusLabel("System ready.");

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btnPanel,         BorderLayout.CENTER);
        bottom.add(bookStatusLabel,  BorderLayout.SOUTH);

        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(bottom,  BorderLayout.SOUTH);

        // Actions
        btnRefresh.addActionListener(e -> refreshBookTable());

        btnBorrow.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame,
                "Enter Student ID (e.g. S001):", "Borrow Book",
                JOptionPane.QUESTION_MESSAGE);
            if (sid == null || sid.trim().isEmpty()) return;
            String bid = JOptionPane.showInputDialog(frame,
                "Enter Book ID (e.g. B001):", "Borrow Book",
                JOptionPane.QUESTION_MESSAGE);
            if (bid == null || bid.trim().isEmpty()) return;
            manager.borrowBook(sid.trim(), bid.trim());
            refreshBookTable();
            setBookStatus("Borrow processed for student " + sid);
        });

        btnReturn.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame,
                "Enter Student ID:", "Return Book",
                JOptionPane.QUESTION_MESSAGE);
            if (sid == null || sid.trim().isEmpty()) return;
            String bid = JOptionPane.showInputDialog(frame,
                "Enter Book ID:", "Return Book",
                JOptionPane.QUESTION_MESSAGE);
            if (bid == null || bid.trim().isEmpty()) return;
            manager.returnBook(sid.trim(), bid.trim());
            refreshBookTable();
            setBookStatus("Return processed for student " + sid);
        });

        btnStudents.addActionListener(e -> showStudentsDialog());

        return panel;
    }

    // ════════════════════════════════════════
    //  BOXES TAB
    // ════════════════════════════════════════
    private JPanel buildBoxesTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // ── Top: box list table ──
        String[] boxCols = {"Box ID", "Bloc", "Floor", "Status"};
        boxTableModel = new DefaultTableModel(boxCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        boxTable = new JTable(boxTableModel);
        styleTable(boxTable);
        JScrollPane boxScroll = new JScrollPane(boxTable);
        boxScroll.setBorder(BorderFactory.createTitledBorder("Available Study Boxes"));
        boxScroll.setPreferredSize(new Dimension(900, 220));

        // ── Middle: bookings table ──
        String[] bkCols = {"Booking ID", "Student", "Box", "Date", "Start", "End", "Status"};
        bookingTableModel = new DefaultTableModel(bkCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        styleTable(bookingTable);
        JScrollPane bkScroll = new JScrollPane(bookingTable);
        bkScroll.setBorder(BorderFactory.createTitledBorder("Today's Bookings"));

        // Split pane
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, boxScroll, bkScroll);
        splitPane.setDividerLocation(220);

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(new Color(240, 240, 240));
        JButton btnRefresh  = createButton("Refresh",       new Color(30, 60, 114));
        JButton btnBook     = createButton("Book a Box",    new Color(0, 128, 0));
        JButton btnCancel   = createButton("Cancel Booking",new Color(180, 60, 0));
        JButton btnCheck    = createButton("Check My Hours",new Color(100, 0, 130));
        btnPanel.add(btnRefresh);
        btnPanel.add(btnBook);
        btnPanel.add(btnCancel);
        btnPanel.add(btnCheck);

        // Status
        boxStatusLabel = createStatusLabel("Select a box to book.");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btnPanel,       BorderLayout.CENTER);
        bottom.add(boxStatusLabel, BorderLayout.SOUTH);

        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(bottom,    BorderLayout.SOUTH);

        // ── Actions ──
        btnRefresh.addActionListener(e -> {
            refreshBoxTable();
            refreshBookingTable();
            setBoxStatus("Refreshed.");
        });

        btnBook.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame,
                "Enter Student ID (e.g. S001):", "Book a Box",
                JOptionPane.QUESTION_MESSAGE);
            if (sid == null || sid.trim().isEmpty()) return;

            String boxId = JOptionPane.showInputDialog(frame,
                "Enter Box ID (e.g. A1B04):", "Book a Box",
                JOptionPane.QUESTION_MESSAGE);
            if (boxId == null || boxId.trim().isEmpty()) return;

            String startStr = JOptionPane.showInputDialog(frame,
                "Enter start time (HH:MM, e.g. 09:00):", "Book a Box",
                JOptionPane.QUESTION_MESSAGE);
            if (startStr == null || startStr.trim().isEmpty()) return;

            String endStr = JOptionPane.showInputDialog(frame,
                "Enter end time (HH:MM, e.g. 11:00):", "Book a Box",
                JOptionPane.QUESTION_MESSAGE);
            if (endStr == null || endStr.trim().isEmpty()) return;

            try {
                LocalTime start = LocalTime.parse(startStr.trim());
                LocalTime end   = LocalTime.parse(endStr.trim());

                Student student = manager.getStudentById(sid.trim());
                if (student == null) {
                    JOptionPane.showMessageDialog(frame,
                        "Student not found: " + sid,
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boxManager.bookBox(student, boxId.trim(), start, end);
                refreshBoxTable();
                refreshBookingTable();
                setBoxStatus("Box " + boxId + " booked successfully!");
                JOptionPane.showMessageDialog(frame,
                    "Box booked successfully!\n"
                    + "Box: " + boxId + "\n"
                    + "Time: " + start + " - " + end,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (com.university.exceptions.BoxNotAvailableException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Cannot book: " + ex.getMessage(),
                    "Box Not Available", JOptionPane.WARNING_MESSAGE);
                setBoxStatus("Booking failed: " + ex.getMessage());

            } catch (com.university.exceptions.BookingLimitExceededException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Cannot book: " + ex.getMessage(),
                    "Daily Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                setBoxStatus("Limit exceeded: " + ex.getMessage());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Invalid time format. Use HH:MM (e.g. 09:00)",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            String bid = JOptionPane.showInputDialog(frame,
                "Enter first 8 characters of Booking ID:", "Cancel Booking",
                JOptionPane.QUESTION_MESSAGE);
            if (bid == null || bid.trim().isEmpty()) return;
            boxManager.cancelBooking(bid.trim());
            refreshBoxTable();
            refreshBookingTable();
            setBoxStatus("Booking cancelled.");
        });

        btnCheck.addActionListener(e -> {
            String sid = JOptionPane.showInputDialog(frame,
                "Enter Student ID:", "Check Remaining Hours",
                JOptionPane.QUESTION_MESSAGE);
            if (sid == null || sid.trim().isEmpty()) return;
            long remaining = boxManager.getRemainingMinutesToday(sid.trim());
            long hours     = remaining / 60;
            long mins      = remaining % 60;
            JOptionPane.showMessageDialog(frame,
                "Student " + sid + " has:\n"
                + hours + " hour(s) and " + mins + " minute(s) remaining today.",
                "Remaining Time", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    // ════════════════════════════════════════
    //  REFRESH METHODS
    // ════════════════════════════════════════
    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        for (com.university.models.Book b : manager.getAllBooks()) {
            String status = b.isAvailable() ? "Available"
                : "Borrowed by " + b.getBorrowedBy().getName();
            bookTableModel.addRow(new Object[]{
                b.getItemId(), b.getTitle(), b.getAuthor(), status
            });
        }
        bookTable.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel,
                        boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    String st = (String) t.getValueAt(row, 3);
                    if (!sel) setBackground(st.equals("Available")
                        ? new Color(220, 255, 220)
                        : new Color(255, 220, 220));
                    return this;
                }
            });
        setBookStatus("Books refreshed. Total: " + bookTableModel.getRowCount());
    }

    private void refreshBoxTable() {
        boxTableModel.setRowCount(0);
        Collection<Box> allBoxes = boxManager.getAllBoxes();
        for (Box b : allBoxes) {
            boxTableModel.addRow(new Object[]{
                b.getBoxId(), b.getBloc(), b.getFloor(),
                b.isAvailable() ? "Available" : "Booked"
            });
        }
        boxTable.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel,
                        boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    String st = (String) t.getValueAt(row, 3);
                    if (!sel) setBackground(st.equals("Available")
                        ? new Color(220, 255, 220)
                        : new Color(255, 220, 220));
                    return this;
                }
            });
    }

    private void refreshBookingTable() {
        bookingTableModel.setRowCount(0);
        for (BoxBooking b : boxManager.getAllBookings()) {
            bookingTableModel.addRow(new Object[]{
                b.getBookingId().substring(0, 8),
                b.getStudent().getName(),
                b.getBox().getBoxId(),
                b.getBookingDate(),
                b.getStartTime(),
                b.getEndTime(),
                b.getStatus()
            });
        }
    }

    // ════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════
    private void showStudentsDialog() {
        StringBuilder sb = new StringBuilder();
        for (Student s : manager.getAllStudents()) {
            sb.append(s.getStudentId()).append(" | ")
              .append(s.getName()).append(" | Has book: ")
              .append(s.hasBorrowedBook()
                  ? s.getCurrentBook().getTitle() : "None")
              .append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        JOptionPane.showMessageDialog(frame,
            new JScrollPane(area), "Students",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(30, 60, 114));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel("  " + text);
        label.setFont(new Font("Arial", Font.ITALIC, 13));
        label.setForeground(new Color(30, 60, 114));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        label.setOpaque(true);
        label.setBackground(new Color(225, 235, 255));
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setBookStatus(String msg) {
        bookStatusLabel.setText("  " + msg);
    }

    private void setBoxStatus(String msg) {
        boxStatusLabel.setText("  " + msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI());
    }
}
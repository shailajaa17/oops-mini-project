import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * A simple Java Swing application for a Student Attendance System.
 *
 * Features:
 * - Input fields for Student Name and Roll Number
 * - A checkbox to mark Present (checked) or Absent (unchecked)
 * - Buttons: Mark Attendance, View Attendance, Exit
 * - Saves attendance records to a text file named "attendance.txt"
 * - Displays saved records in a JTextArea
 *
 * This code is beginner-friendly and contains comments explaining each part.
 */
public class StudentAttendanceSystem extends JFrame {

    // UI components declared at the class level so we can access them in button handlers
    private JTextField nameField;
    private JTextField rollField;
    private JCheckBox presentCheckBox; // Checked => Present, Unchecked => Absent
    private JTextArea displayArea;     // Shows attendance records when requested

    // File name where attendance will be saved
    private static final String ATTENDANCE_FILE = "attendance.txt";

    /**
     * Constructor sets up the window and adds all components.
     */
    public StudentAttendanceSystem() {
        // Set the title of the main window (JFrame)
        super("Student Attendance System");

        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        formPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Roll Number:"));
        rollField = new JTextField(10);
        formPanel.add(rollField);

        presentCheckBox = new JCheckBox("Present (unchecked = Absent)");
        presentCheckBox.setSelected(true);
        formPanel.add(presentCheckBox);
        formPanel.add(new JLabel(""));

        JButton markButton = new JButton("Mark Attendance");
        JButton viewButton = new JButton("View Attendance");
        JButton summaryButton = new JButton("Summary");
        JButton exitButton = new JButton("Exit");

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.add(markButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(summaryButton);
        buttonsPanel.add(exitButton);

        displayArea = new JTextArea(12, 40);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Wire up button actions using lambda expressions for brevity
        markButton.addActionListener(this::handleMarkAttendance);
        viewButton.addActionListener(this::handleViewAttendance);
        summaryButton.addActionListener(this::handleShowSummary);
        exitButton.addActionListener(e -> System.exit(0));

        // Basic JFrame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();                  // size the window to fit its components
        setLocationRelativeTo(null); // center on screen
        setVisible(true);        // show the window
    }

    /**
     * Handles the logic when the "Mark Attendance" button is clicked.
     * - Validates inputs
     * - Appends a line to attendance.txt with Name, Roll No, and Status
     */
    private void handleMarkAttendance(ActionEvent e) {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();

        // Simple input validation
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the student name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the roll number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = presentCheckBox.isSelected() ? "Present" : "Absent";
        String today = LocalDate.now().toString();

        // Prevent duplicate for same roll on the same day
        if (existsRecordForRollOnDate(roll, today)) {
            JOptionPane.showMessageDialog(this, "Attendance already marked today for roll: " + roll, "Duplicate", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String line = name + "," + roll + "," + status + "," + today + System.lineSeparator();

        try (FileOutputStream fos = new FileOutputStream(ATTENDANCE_FILE, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            writer.write(line);
            writer.flush();

            // Notify user and clear inputs
            JOptionPane.showMessageDialog(this, "Attendance marked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            rollField.setText("");
            presentCheckBox.setSelected(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the logic when the "View Attendance" button is clicked.
     * - Reads the contents of attendance.txt
     * - Displays them in the JTextArea
     */
    private void handleViewAttendance(ActionEvent e) {
        File file = new File(ATTENDANCE_FILE);

        // If the file doesn't exist yet, inform the user and clear the display
        if (!file.exists()) {
            displayArea.setText("");
            JOptionPane.showMessageDialog(this, "No attendance records found yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Read the entire file and show it
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }

            displayArea.setText(builder.toString());
            displayArea.setCaretPosition(0); // scroll to top
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Show summary: totals and per-roll statistics
    private void handleShowSummary(ActionEvent e) {
        List<String[]> records = readAllRecords();
        if (records.isEmpty()) {
            displayArea.setText("");
            JOptionPane.showMessageDialog(this, "No attendance records found yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int total = 0;
        int present = 0;
        Map<String, int[]> perRoll = new LinkedHashMap<>(); // roll -> [present, total]

        for (String[] r : records) {
            if (r.length < 3) continue;
            String roll = r[1];
            String status = r[2];
            total++;
            boolean isPresent = "Present".equalsIgnoreCase(status);
            if (isPresent) present++;
            int[] arr = perRoll.computeIfAbsent(roll, k -> new int[]{0, 0});
            if (isPresent) arr[0]++;
            arr[1]++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SUMMARY").append(System.lineSeparator());
        sb.append("Total Records: ").append(total).append(System.lineSeparator());
        sb.append("Present: ").append(present).append(" | Absent: ").append(total - present).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Per Roll Number:").append(System.lineSeparator());
        for (Map.Entry<String, int[]> entry : perRoll.entrySet()) {
            int p = entry.getValue()[0];
            int t = entry.getValue()[1];
            int a = t - p;
            double pct = t > 0 ? (p * 100.0 / t) : 0.0;
            sb.append("Roll ").append(entry.getKey()).append(": Present=").append(p)
              .append(", Absent=").append(a)
              .append(", %Present=")
              .append(String.format(Locale.US, "%.1f", pct))
              .append('%')
              .append(System.lineSeparator());
        }

        displayArea.setText(sb.toString());
        displayArea.setCaretPosition(0);
    }

    // Read all records from the file, parsing CSV lines as [name, roll, status, date?]
    private List<String[]> readAllRecords() {
        File file = new File(ATTENDANCE_FILE);
        List<String[]> list = new ArrayList<>();
        if (!file.exists()) return list;
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                list.add(parts);
            }
        } catch (IOException ignored) {
        }
        return list;
    }

    // Check duplicate: same roll and date
    private boolean existsRecordForRollOnDate(String roll, String date) {
        List<String[]> records = readAllRecords();
        for (String[] r : records) {
            if (r.length >= 4) {
                if (roll.equals(r[1]) && date.equals(r[3])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The main entry point. Creates and shows the application window on the Swing event dispatch thread.
     */
    public static void main(String[] args) {
        // Always start Swing apps on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(StudentAttendanceSystem::new);
    }
}

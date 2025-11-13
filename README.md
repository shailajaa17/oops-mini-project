# 🧑‍🎓 Student Attendance System (Java Swing)

A simple *Java Swing-based desktop application* to manage and record student attendance.  
This beginner-friendly project demonstrates the use of *Swing GUI, **file handling, and **basic event-driven programming* in Java — all without using any external database.

---

## 🚀 Features

- Add a student's *Name* and *Roll Number*.
- Mark students as *Present* or *Absent* using a checkbox.
- Save attendance records to a local file (attendance.txt).
- View all saved attendance data directly inside the app.
- Simple UI built with JFrame, JTextField, JCheckBox, JButton, and JTextArea.
- Lightweight and fully *self-contained* — no setup required beyond Java.

---

## 🧰 Technologies Used

- *Java (JDK 8 or higher)*
- *Swing* (for GUI)
- *File I/O* (for saving and reading attendance)

---

## 📂 File Structure

StudentAttendanceSystem/ ├── StudentAttendanceSystem.java   # Main Java program file ├── attendance.txt                 # Stores attendance records (auto-created) └── README.md                      # Project documentation

---

## 🧑‍💻 How to Run

1. Make sure you have *Java JDK* installed on your computer.  
   (You can check using: java -version)

2. Save the project files in a folder (e.g., StudentAttendanceSystem).

3. Open the folder in your code editor (like VS Code or IntelliJ IDEA).

4. Compile the program:
   ```bash
   javac StudentAttendanceSystem.java

5. Run the program:

java StudentAttendanceSystem


6. The app window will open. You can:

Enter Student Name and Roll Number.

Tick the checkbox for Present (unticked = Absent).

Click "Mark Attendance" to save the record.

Click "View Attendance" to see all saved data.

Click "Exit" to close the app.





---

📄 Example Output

When viewing attendance, you’ll see something like:

Name: Shailajaa | Roll No: 101 | Status: Present
Name: Prejitha | Roll No: 102 | Status: Absent


---

💾 Data Storage

Attendance records are saved in a text file named attendance.txt
Each entry includes:

Name: <Student Name> | Roll No: <Roll Number> | Status: <Present/Absent>

This file is automatically created in the same directory as the .java file.


---

🧠 Learning Outcomes

By building this project, you’ll learn:

How to create GUI applications using Java Swing.

How to handle button click events using ActionListener.

How to use FileWriter and BufferedReader for data storage.

Basic project structure and documentation with README.md.



---

🏁 Future Improvements

Add date and time for each attendance record.

Include a student list and filtering options.

Improve UI design with better layouts and colors.

Allow exporting reports as .csv.

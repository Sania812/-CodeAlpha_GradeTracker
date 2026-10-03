/**
 * Student.java
 * Represents one student: name, roll number, and marks.
 * This class uses ENCAPSULATION - all fields are private,
 * and are accessed only through public getter methods.
 */
public class Student {
    private String name;
    private int rollNumber;
    private double marks;

    public Student(String name, int rollNumber, double marks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public double getMarks() {
        return marks;
    }

    /**
     * Works out the letter grade from the marks.
     */
    public String getGrade() {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B";
        else if (marks >= 60) return "C";
        else if (marks >= 50) return "D";
        else return "F";
    }

    /**
     * Returns a neatly formatted row for the report table.
     */
    @Override
    public String toString() {
        return String.format("%-5d %-20s %-10.2f %-5s", rollNumber, name, marks, getGrade());
    }
}

import java.util.ArrayList;

/**
 * GradeTracker.java
 * Manages the collection of Student objects.
 * Uses an ArrayList<Student> so we can add as many students as we want
 * without worrying about a fixed array size.
 */
public class GradeTracker {
    private ArrayList<Student> students;

    public GradeTracker() {
        students = new ArrayList<>();
    }

    public void addStudent(Student s) {
        students.add(s);
    }

    /**
     * Replaces the student at rollNumber with an updated Student object.
     * Used by the GUI when the user edits an existing student.
     */
    public void updateStudent(int rollNumber, Student updated) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getRollNumber() == rollNumber) {
                students.set(i, updated);
                return;
            }
        }
    }

    public void removeStudent(int rollNumber) {
        students.removeIf(s -> s.getRollNumber() == rollNumber);
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public int getTotalStudents() {
        return students.size();
    }

    public double getAverageMarks() {
        double sum = 0;
        for (Student s : students) {
            sum += s.getMarks();
        }
        return sum / students.size();
    }

    public Student getHighestScorer() {
        Student highest = students.get(0);
        for (Student s : students) {
            if (s.getMarks() > highest.getMarks()) {
                highest = s;
            }
        }
        return highest;
    }

    public Student getLowestScorer() {
        Student lowest = students.get(0);
        for (Student s : students) {
            if (s.getMarks() < lowest.getMarks()) {
                lowest = s;
            }
        }
        return lowest;
    }

    /**
     * Searches for a student by roll number.
     * Returns null if no student is found.
     */
    public Student searchByRollNumber(int rollNumber) {
        for (Student s : students) {
            if (s.getRollNumber() == rollNumber) {
                return s;
            }
        }
        return null;
    }

    /**
     * Searches for a student by name (case-insensitive).
     * Returns null if no student is found.
     */
    public Student searchByName(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Prints a clean, formatted report of all students.
     */
    public void displayAllStudents() {
        System.out.println("\n==================== ALL STUDENTS ====================");
        System.out.printf("%-5s %-20s %-10s %-5s%n", "Roll", "Name", "Marks", "Grade");
        System.out.println("-------------------------------------------------------");
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println("=======================================================");
    }

    /**
     * Prints the statistics summary: total students, average,
     * highest scorer, and lowest scorer.
     */
    public void displayStatistics() {
        System.out.println("\n==================== STATISTICS ====================");
        System.out.println("Total Students : " + getTotalStudents());
        System.out.printf("Average Marks  : %.2f%n", getAverageMarks());

        Student highest = getHighestScorer();
        Student lowest = getLowestScorer();

        System.out.printf("Highest Marks  : %.2f (%s, Roll No: %d)%n",
                highest.getMarks(), highest.getName(), highest.getRollNumber());
        System.out.printf("Lowest Marks   : %.2f (%s, Roll No: %d)%n",
                lowest.getMarks(), lowest.getName(), lowest.getRollNumber());
        System.out.println("======================================================");
    }
}

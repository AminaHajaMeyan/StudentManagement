package studentmanagement;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.Connection;

public class StudentManagement {
    // Scanner object for user input.
    static Scanner scanner = new Scanner(System.in);
    
    private static DbConfig dbconfig = DbConfig.getInstance();
        
    public static void main(String[] args) throws Exception {
        // Start the application with the dashboard method.
        dashboard();
    }
    
    public static void dashboard() {
        // Display the dashboard menu options.
        System.out.println("\n\t-- Welcome to the Student Management Application!! --");
        System.out.println("\n****************************************************");
        System.out.println("*                   MENU OPTIONS                   *");
        System.out.println("****************************************************");
        System.out.println("\t1) Add Student.");
        System.out.println("\t2) Edit Student");
        System.out.println("\t3) Remove Student");
        System.out.println("\t4) View all Students");
        System.out.println("\t5) Search Student");
        System.out.println("\t6) Quit");
        System.out.println("****************************************************\n");

        try {
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Perform action based on user choice.
            switch (choice) {
                case 1:
                    addStudent();
                    break;
                 case 2:
                     editStudent();
                     break;
                 case 3:
                     deleteStudent();
                     break;
                 case 4:
                     viewAllStudents();
                     break;
                 case 5:
                     searchStudent();
                     break;
                case 6:
                    // Exit the application.
                    System.exit(0);
                    break;
                default:
                    System.out.print("\033[H\033[2J");
                    System.out.println("Invalid option. Please enter a valid option between 1 and 6.");
                    dashboard();
            }
        } catch (InputMismatchException e) {
            System.out.print("\033[H\033[2J");
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine(); // Clear the invalid input
            dashboard();
        }
    }
    
    public static void addStudent() {
        System.out.print("\033[H\033[2J");
        System.out.println("\t--------- * ---------");
        System.out.println("\t     Add Students      ");
        System.out.println("\t--------- * ---------");

        try {
            // Collect student details from the user.
            System.out.print("\nEnter student Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter student Gender: ");
            String gender = scanner.nextLine();

            String age;
            while (true) {
                System.out.print("Enter student Age: ");
                age = scanner.nextLine();
                if (isValidAge(age)) {
                    break;
                } else {
                    System.out.println("Please enter a valid age.");
                }
            }

            System.out.print("Enter student Date of Birth (DD/MM/YYYY): ");
            String dob = scanner.nextLine();

            String contactNumber;
            while (true) {
                System.out.print("Enter student Contact Number: ");
                contactNumber = scanner.nextLine();
                if (isValidPhoneNumber(contactNumber)) {
                    break;
                } else {
                    System.out.println("Please enter a valid contact number.");
                }
            }

            // Add student to the database.
            String sql = "INSERT INTO student(name, age, gender, dob, contact_number)" + " VALUES (?, ?, ?, ?, ?)";
            try (Connection con = dbconfig.dbConnection()) {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, Integer.parseInt(age));
                ps.setString(3, gender);
                ps.setString(4, dob);
                ps.setString(5, contactNumber);

                int row = ps.executeUpdate();
                System.out.println(row + " student(s) added.");
            }

            System.out.println("\nStudent added successfully.");

            // Ask user if they want to add another student.
            System.out.print("Do you want to add another student? Press \"Y\" for Yes or \"N\" for No: ");
            String selection = scanner.nextLine();

            if (selection.equalsIgnoreCase("y")) {
                addStudent();
            } else {
                System.out.print("\033[H\033[2J");
                dashboard();
            }
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Invalid input or database error. Please try again.");
            scanner.nextLine(); // Clear the invalid input
            addStudent(); // Retry adding a student
        }
    }
    
    private static boolean isValidAge(String age) {
        try {
            int ageInt = Integer.parseInt(age);
            return ageInt > 0 && ageInt < 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }
    
    public static void viewAllStudents() {
        System.out.print("\033[H\033[2J");
        System.out.println("\t--------- * ---------");
        System.out.println("\t  View all Students  ");
        System.out.println("\t--------- * ---------\n");

        try {
            String sql = "SELECT * FROM student";
            try (Connection con = dbconfig.dbConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                if (!rs.isBeforeFirst()) {
                    System.out.println("No students found.");
                } else {
                    System.out.println("ID\tName\t\tAge\tGender\tDOB\t\tContact Number");
                    System.out.println("--------------------------------------------------------------------");
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        int age = rs.getInt("age");
                        String gender = rs.getString("gender");
                        String dob = rs.getString("dob");
                        String contactNumber = rs.getString("contact_number");

                        System.out.println(id + "\t" + name + "\t\t" + age + "\t" + gender + "\t" + dob + "\t" + contactNumber);
                        
                    }
                }
            }
            dashboard(); // Return to main menu
        } catch (SQLException e) {
            System.out.println("Database error. Please try again.");
            dashboard(); // Return to main menu
        }
    }

    public static void editStudent() {
       System.out.print("\033[H\033[2J");
        System.out.println("\t--------- * ---------");
        System.out.println("\t    Edit Student    ");
        System.out.println("\t--------- * ---------\n");

        try {
            System.out.print("Enter student ID to edit: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Check if student exists
            if (!isStudentExists(id)) {
                System.out.println("Student with ID " + id + " does not exist.\n");
                dashboard();
                return;
            }

            // Input updated details
            System.out.print("Enter updated Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter updated Gender: ");
            String gender = scanner.nextLine();

            int age;
            while (true) {
                System.out.print("Enter updated Age: ");
                try {
                    age = scanner.nextInt();
                    if (age > 0 && age < 100) {
                        break;
                    } else {
                        System.out.println("Please enter a valid age (1-99).");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer for age.");
                    scanner.nextLine(); // Clear the invalid input
                }
            }
            scanner.nextLine(); // Consume newline

            System.out.print("Enter updated Date of Birth (DD/MM/YYYY): ");
            String dob = scanner.nextLine();

            String contactNumber;
            while (true) {
                System.out.print("Enter updated Contact Number: ");
                contactNumber = scanner.nextLine();
                if (isValidPhoneNumber(contactNumber)) {
                    break;
                } else {
                    System.out.println("Please enter a valid 10-digit contact number.");
                }
            }

            // Update student in database
            String sql = "UPDATE student SET name = ?, age = ?, gender = ?, dob = ?, contact_number = ? WHERE id = ?";
            try (Connection con = dbconfig.dbConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setInt(2, age);
                ps.setString(3, gender);
                ps.setString(4, dob);
                ps.setString(5, contactNumber);
                ps.setInt(6, id);

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("\nStudent with ID " + id + " updated successfully.\n");
                } else {
                    System.out.println("\nFailed to update student. Please try again.\n");
                }
            } catch (SQLException e) {
                System.out.println("Database error. Please try again.\n");
            }

            dashboard(); // Return to main menu
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer for student ID.\n");
            scanner.nextLine(); // Clear the invalid input
            editStudent(); // Retry editing a student
        }
    }
    private static boolean isStudentExists(int id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        try (Connection con = dbconfig.dbConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Returns true if student exists
            }
        } catch (SQLException e) {
            System.out.println("Database error. Please try again.\n");
        }
        return false;
    }


    private static void deleteStudent(){
        System.out.print("\033[H\033[2J");
        System.out.println("\t--------- * ---------");
        System.out.println("\t   Delete Student   ");
        System.out.println("\t--------- * ---------\n");

        try {
            System.out.print("Enter student ID to delete: ");
            int studentId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Check if the student ID exists in the database
            String checkSql = "SELECT * FROM student WHERE id = ?";
            try (Connection con = dbconfig.dbConnection();
                 PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setInt(1, studentId);
                ResultSet rs = checkPs.executeQuery();

                if (!rs.next()) {
                    System.out.print("\033[H\033[2J");
                    System.out.println("Student ID not found.");
                    dashboard();
                    return;
                }
            }

            // Confirm deletion
            System.out.print("Are you sure you want to delete this student? Press \"Y\" for Yes or \"N\" for No: ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("y")) {
                // Delete student from the database
                String deleteSql = "DELETE FROM student WHERE id = ?";
                try (Connection con = dbconfig.dbConnection();
                     PreparedStatement ps = con.prepareStatement(deleteSql)) {
                    ps.setInt(1, studentId);

                    int row = ps.executeUpdate();
                    System.out.println(row + " student(s) deleted.");
                }
                System.out.println("\nStudent deleted successfully.");
            } else {
                System.out.println("Deletion canceled.");
            }

            dashboard(); // Return to main menu
        } catch (InputMismatchException | SQLException e) {
            System.out.println("Invalid input or database error. Please try again.");
            scanner.nextLine(); // Clear the invalid input
            deleteStudent(); // Retry deleting a student
        }
    }


    public static void searchStudent() {
        System.out.print("\033[H\033[2J");
        System.out.println("\t--------- * ---------");
        System.out.println("\t   Search Student   ");
        System.out.println("\t--------- * ---------\n");

        try {
            System.out.print("Enter student ID to search: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String sql = "SELECT * FROM student WHERE id = ?";
            try (Connection con = dbconfig.dbConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (!rs.isBeforeFirst()) {
                    System.out.println("No student found with ID: " + id + "\n");
                } else {
                    System.out.println("ID\tName\t\tAge\tGender\tDOB\t\tContact Number");
                    System.out.println("----------------------------------------------------------");
                    while (rs.next()) {
                        int studentId = rs.getInt("id");
                        String name = rs.getString("name");
                        int age = rs.getInt("age");
                        String gender = rs.getString("gender");
                        String dob = rs.getString("dob");
                        String contactNumber = rs.getString("contact_number");

                        System.out.println(id + "\t" + name + "\t\t" + age + "\t" + gender + "\t" + dob + "\t" + contactNumber);
                    }
                    System.out.println();
                }
            } catch (SQLException e) {
                System.out.println("Database error. Please try again.\n");
            }

            dashboard(); // Return to main menu
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer for student ID.\n");
            scanner.nextLine(); // Clear the invalid input
            searchStudent(); // Retry searching by student ID
        }
    }
}


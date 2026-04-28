package models;

public class Employee {
    private String employee_ssn;
    private String firstName;
    private String lastName;
    private String acc_username;
    private String password;
    private String role;

    public Employee(String employee_ssn, String firstName, String lastName, String acc_username, String password, String role) {
        this.employee_ssn = employee_ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.acc_username = acc_username;
        this.password = password;
        this.role = role;
    }

    public String getEmployeeSsn() {
        return employee_ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return acc_username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}

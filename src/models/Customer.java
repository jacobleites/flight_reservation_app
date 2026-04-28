package models;

public class Customer {
    private String customer_ssn; // do we really need the customers_ssn? Feels a little intrusive for the system
    private String email;
    private String gender;
    private String dob;
    private String firstName;
    private String lastName;
    private String phone;
    private int account_id;
    private String username;
    private String password;

    // primary customer for when we are creatng a customer (minus account_id, as it is auto generated when they make their account)
    public Customer(String ssn, String email, String gender, String dob, String firstName, 
                    String lastName, String phone, String username, String password ) {
        this.customer_ssn = ssn;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    // extra constructor for when we need to UPDATE a customer, so they already have an account id.
    public Customer(String ssn, String email, String gender, String dob, String firstName, 
                    String lastName, String phone, int account_id, String username, String password ) {
        this.customer_ssn = ssn;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.account_id = account_id;
        this.username = username;
        this.password = password;
    }


    public String getSsn() {
        return customer_ssn;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public int getAccount_id() {
        return account_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

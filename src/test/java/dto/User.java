package dto;

import dto.enums.Gender;

public class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final Gender gender;

    public User(String firstName, String lastName, String email, String password, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public User(String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password, null);
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public Gender getGender() {
        return gender;
    }


    public String toString(){
        return "{ User = [Gender: \"" + gender + "\",  FirstName: \"" + firstName + "\", LastName: \"" + lastName
                + "\", Email: \"" + email + "\", Password: \"" + password + "\"] }";
    }
}
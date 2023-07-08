package com.example.my_goals.models;

public class User {
    public String firstName, lastName,userType;

    public User() {
    }

    public User(String firstName, String lastName, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserType() {
        return userType;
    }
}

package com.jubaldo.fthangouts.model;

public class Contact {
    private int id;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String email;
    private final String birthday;

    public Contact(String firstName, String lastName, String phoneNumber, String email, String birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }
}

package com.example.demo.model;

public class DataList {
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String nationality;

    public DataList(String firstName, String lastName,
                    String gender, String dateOfBirth, String nationality) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.nationality = nationality;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getNationality() { return nationality; }
}
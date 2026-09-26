package com.fixit.model;

public abstract class Person {
    protected String name;
    protected String email;
    protected String phoneNo;
    protected String location;
    protected String password;

    public Person(String name, String email, String phoneNo, String location, String password) {
        this.name = name;
        this.email = email;
        setPhoneNo(phoneNo);
        this.location = location;
        setPassword(password);
    }

    public abstract void login();
    public abstract void logout();
    public abstract String viewProfile();

    public void setPhoneNo(String phoneNo) {
        if (phoneNo != null && phoneNo.length() == 11) {
            this.phoneNo = phoneNo;
        } else {
            System.out.println("Please enter a valid phone number of 11 digits");
        }
    }

    public void setPassword(String password) {
        if (password != null && password.length() >= 6) {
            this.password = password;
        } else {
            System.out.println("Please enter a valid password of at least 6 digits ");
        }
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getPassword() {
        return password;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getLocation() { return location; }
}

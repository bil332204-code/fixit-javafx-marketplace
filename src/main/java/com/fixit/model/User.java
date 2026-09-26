package com.fixit.model;

public class User extends Person {

    public User(String name, String email, String phoneNo, String location, String password) {
        super(name, email, phoneNo, location, password);
    }

    @Override
    public void login() {
        System.out.println(name + " Login");
    }

    @Override
    public void logout() {
        System.out.println(name + " Logout");
    }

    @Override
    public String viewProfile() {
        return "=== User Profile ===\n" +
               "Name: " + name + "\n" +
               "Email: " + email + "\n" +
               "Location: " + location + "\n" +
               "Phone: " + getPhoneNo();
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }

    public void rateWorker(Worker w, double rating) {
        if (rating >= 0 && rating <= 5) {
            w.setRating(rating);
            System.out.println(name + " rated " + w.getName() + " " + rating + " stars");
        } else {
            System.out.println("rating must be between 0 and 5");
        }
    }
}

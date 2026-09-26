package com.fixit.model;

public class Worker extends Person {

    private Skill skill;
    private double rating;
    private Availability isAvailable;

    public Worker(String name, String email, String phoneNo, String location, String password, Skill skill,
            Availability isAvailable) {
        super(name, email, phoneNo, location, password);
        this.skill = skill;
        this.isAvailable = isAvailable;
        this.rating = 0.0;
    }

    @Override
    public void login() {
        System.out.println("Worker login...");
    }

    @Override
    public void logout() {
        System.out.println("Worker Logout");
    }

    @Override
    public String viewProfile() {
        return "=== Worker Profile ===\n" +
               "Name: " + name + "\n" +
               "Email: " + email + "\n" +
               "Location: " + location + "\n" +
               "Skill: " + skill + "\n" +
               "Rating: " + rating + "\n" +
               "Status: " + isAvailable;
    }

    @Override
    public String toString() {
        return name + " [" + skill + "] - Rating: " + rating + " - " + isAvailable;
    }

    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Availability getAvailability() {
        return isAvailable;
    }

    public void setAvailability(Availability status) {
        this.isAvailable = status;
    }
}

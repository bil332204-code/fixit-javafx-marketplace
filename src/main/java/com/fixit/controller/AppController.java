package com.fixit.controller;

import com.fixit.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton controller — the central hub connecting UI to data.
 * Manages users, workers, bookings, and session state.
 */
public class AppController {

    private static final AppController INSTANCE = new AppController();

    private final List<User> users = new ArrayList<>();
    private final List<Worker> workers = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    private User currentUser;
    private Worker currentWorker;

    private AppController() {
        // Load data from files, or seed demo data if files don't exist
        loadOrSeedData();
    }

    private void loadOrSeedData() {
        List<User> loaded = DataStore.loadUsers();
        List<Worker> loadedWorkers = DataStore.loadWorkers();
        List<Booking> loadedBookings = DataStore.loadBookings(loaded, loadedWorkers);

        if (loaded.isEmpty() && loadedWorkers.isEmpty()) {
            // First time: seed demo data
            seedDemoData();
            DataStore.saveUsers(users);
            DataStore.saveWorkers(workers);
        } else {
            // Load existing data
            users.addAll(loaded);
            workers.addAll(loadedWorkers);
            bookings.addAll(loadedBookings);
        }
    }

    public static AppController getInstance() {
        return INSTANCE;
    }

    // ────────── Registration ──────────

    public String registerUser(String name, String email, String phone, String location, String password) {
        String error = validateCommon(name, email, phone, password);
        if (error != null) return error;
        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email)))
            return "An account with this email already exists.";
        users.add(new User(name, email, phone, location, password));
        DataStore.saveUsers(users);
        return null; // null = success
    }

    public String registerWorker(String name, String email, String phone, String location,
                                  String password, Skill skill) {
        String error = validateCommon(name, email, phone, password);
        if (error != null) return error;
        if (skill == null) return "Please select a skill.";
        if (workers.stream().anyMatch(w -> w.getEmail().equalsIgnoreCase(email)))
            return "An account with this email already exists.";
        workers.add(new Worker(name, email, phone, location, password, skill, Availability.AVAILABLE));
        DataStore.saveWorkers(workers);
        return null;
    }

    private String validateCommon(String name, String email, String phone, String password) {
        if (name == null || name.trim().isEmpty()) return "Name is required.";
        if (email == null || !email.contains("@") || !email.contains("."))
            return "Please enter a valid email address.";
        if (phone == null || phone.length() != 11 || !phone.matches("\\d+"))
            return "Phone number must be exactly 11 digits.";
        if (password == null || password.length() < 6)
            return "Password must be at least 6 characters.";
        return null;
    }

    // ────────── Authentication ──────────

    public User loginUser(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                currentUser = u;
                currentWorker = null;
                return u;
            }
        }
        return null;
    }

    public Worker loginWorker(String email, String password) {
        for (Worker w : workers) {
            if (w.getEmail().equalsIgnoreCase(email) && w.getPassword().equals(password)) {
                currentWorker = w;
                currentUser = null;
                return w;
            }
        }
        return null;
    }

    public void logout() {
        currentUser = null;
        currentWorker = null;
    }

    // ────────── Bookings ──────────

    public Booking createBooking(Worker worker, String description, LocalDate date) {
        if (currentUser == null) return null;
        Booking b = new Booking(currentUser, worker, description, date);
        bookings.add(b);
        DataStore.saveBookings(bookings);
        return b;
    }

    public void rateBooking(Booking booking, double rating) {
        if (currentUser == null || booking == null) return;
        if (booking.getClient().getEmail().equals(currentUser.getEmail())) {
            currentUser.rateWorker(booking.getWorker(), rating);
            booking.setRated(true);
            DataStore.saveBookings(bookings);
            DataStore.saveWorkers(workers);
        }
    }

    public List<Booking> getBookingsForUser() {
        if (currentUser == null) return new ArrayList<>();
        return bookings.stream()
                .filter(b -> b.getClient().getEmail().equals(currentUser.getEmail()))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsForWorker() {
        if (currentWorker == null) return new ArrayList<>();
        return bookings.stream()
                .filter(b -> b.getWorker().getEmail().equals(currentWorker.getEmail()))
                .collect(Collectors.toList());
    }

    // ────────── Search / Filter ──────────

    public List<Worker> searchWorkers(String query, Skill skillFilter) {
        return workers.stream().filter(w -> {
            boolean matches = true;
            if (query != null && !query.trim().isEmpty()) {
                String q = query.toLowerCase();
                matches = w.getName().toLowerCase().contains(q)
                        || w.getLocation().toLowerCase().contains(q);
            }
            if (skillFilter != null) {
                matches = matches && w.getSkill() == skillFilter;
            }
            return matches;
        }).collect(Collectors.toList());
    }

    // ────────── Getters ──────────

    public User getCurrentUser() { return currentUser; }
    public Worker getCurrentWorker() { return currentWorker; }
    public List<User> getUsers() { return users; }
    public List<Worker> getWorkers() { return workers; }
    public List<Booking> getAllBookings() { return bookings; }

    // ────────── Demo Data ──────────

    private void seedDemoData() {
        workers.add(new Worker("Ahmed Khan", "worker1@example.com", "03000000001", "Lahore",
                "demo123", Skill.ELECTRICIAN, Availability.AVAILABLE));
        workers.add(new Worker("Bilal Hussain", "worker2@example.com", "03000000002", "Karachi",
                "demo123", Skill.PLUMBER, Availability.AVAILABLE));
        workers.add(new Worker("Usman Ali", "worker3@example.com", "03000000003", "Islamabad",
                "demo123", Skill.CARPENTER, Availability.BUSY));
        workers.add(new Worker("Farhan Raza", "worker4@example.com", "03000000004", "Lahore",
                "demo123", Skill.PAINTER, Availability.AVAILABLE));
        workers.add(new Worker("Kamran Yousuf", "worker5@example.com", "03000000005", "Rawalpindi",
                "demo123", Skill.CAR_MECHANIC, Availability.OFFLINE));
        workers.add(new Worker("Zain Malik", "worker6@example.com", "03000000006", "Faisalabad",
                "demo123", Skill.BIKE_MECHANIC, Availability.AVAILABLE));

        // Give some workers ratings
        workers.get(0).setRating(4.8);
        workers.get(1).setRating(4.5);
        workers.get(2).setRating(4.2);
        workers.get(3).setRating(3.9);
        workers.get(4).setRating(4.6);
        workers.get(5).setRating(4.0);

        // Seed a demo user
        users.add(new User("Demo User", "demo@example.com", "03000000000", "Lahore", "demo123"));
    }
}

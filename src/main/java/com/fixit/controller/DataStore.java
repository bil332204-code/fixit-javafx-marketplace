package com.fixit.controller;

import com.fixit.model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles file persistence for users, workers, and bookings.
 * Data is saved to CSV files in the project root directory.
 */
public class DataStore {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String WORKERS_FILE = DATA_DIR + "/workers.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "/bookings.txt";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                writer.println(u.getName() + "|" + u.getEmail() + "|" + u.getPhoneNo() + "|" + 
                              u.getLocation() + "|" + u.getPassword());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static void saveWorkers(List<Worker> workers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WORKERS_FILE))) {
            for (Worker w : workers) {
                writer.println(w.getName() + "|" + w.getEmail() + "|" + w.getPhoneNo() + "|" + 
                              w.getLocation() + "|" + w.getPassword() + "|" + w.getSkill() + "|" +
                              w.getAvailability() + "|" + w.getRating());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Worker> loadWorkers() {
        List<Worker> workers = new ArrayList<>();
        File file = new File(WORKERS_FILE);
        if (!file.exists()) return workers;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    Worker w = new Worker(parts[0], parts[1], parts[2], parts[3], parts[4],
                                        Skill.valueOf(parts[5]), Availability.valueOf(parts[6]));
                    w.setRating(Double.parseDouble(parts[7]));
                    workers.add(w);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workers;
    }

    public static void saveBookings(List<Booking> bookings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) {
                // Store client and worker emails to recover objects
                writer.println(b.getId() + "|" + b.getClient().getEmail() + "|" + 
                              b.getWorker().getEmail() + "|" + b.getDescription() + "|" +
                              b.getScheduledDate() + "|" + b.getStatus() + "|" + b.isRated());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Booking> loadBookings(List<User> users, List<Worker> workers) {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return bookings;

        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    int bookingId = Integer.parseInt(parts[0]);
                    maxId = Math.max(maxId, bookingId);

                    User client = users.stream()
                            .filter(u -> u.getEmail().equals(parts[1]))
                            .findFirst().orElse(null);
                    Worker worker = workers.stream()
                            .filter(w -> w.getEmail().equals(parts[2]))
                            .findFirst().orElse(null);

                    if (client != null && worker != null) {
                        Booking b = new Booking(client, worker, parts[3], 
                                              java.time.LocalDate.parse(parts[4]));
                        b.setStatus(BookingStatus.valueOf(parts[5]));
                        b.setRated(Boolean.parseBoolean(parts[6]));
                        bookings.add(b);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (maxId > 0) {
            Booking.setIdCounter(maxId + 1);
        }
        return bookings;
    }
}

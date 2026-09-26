package com.fixit.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private static int idCounter = 1;

    private int id;
    private User client;
    private Worker worker;
    private String description;
    private LocalDate scheduledDate;
    private LocalDateTime createdAt;
    private BookingStatus status;
    private boolean isRated;

    public Booking(User client, Worker worker, String description, LocalDate scheduledDate) {
        this.id = idCounter++;
        this.client = client;
        this.worker = worker;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.isRated = false;
    }

    public static void setIdCounter(int counter) {
        idCounter = counter;
    }

    public int getId() { return id; }
    public User getClient() { return client; }
    public Worker getWorker() { return worker; }
    public String getDescription() { return description; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public BookingStatus getStatus() { return status; }
    public boolean isRated() { return isRated; }

    public void setStatus(BookingStatus status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public void setRated(boolean rated) { this.isRated = rated; }

    @Override
    public String toString() {
        return "Booking #" + id + " | " + worker.getName() + " (" + worker.getSkill() + ") | " +
               scheduledDate + " | " + status;
    }
}

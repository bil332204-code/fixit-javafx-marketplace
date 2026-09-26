package com.fixit.view;

import com.fixit.MainApp;
import com.fixit.controller.AppController;
import com.fixit.model.*;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.time.LocalDate;
import java.util.List;

public class UserDashboardView {

    private final MainApp app;
    private final AppController ctrl;
    private VBox contentArea;

    public UserDashboardView(MainApp app, AppController ctrl) {
        this.app = app;
        this.ctrl = ctrl;
    }

    public Pane createView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dark-bg");

        // ── Sidebar ──
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(230);
        sidebar.setPadding(new Insets(20, 0, 20, 0));

        Label brand = new Label("🔧 FixIt");
        brand.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:#FF6B35; -fx-padding: 10 24 20 24;");

        Label userName = new Label("Hello, " + ctrl.getCurrentUser().getName());
        userName.setStyle("-fx-text-fill:#CCCCDD; -fx-font-size:13px; -fx-padding: 0 24 16 24;");

        Separator sep = new Separator();
        sep.setStyle("-fx-padding: 0 12;");

        Button btnHome = sidebarBtn("🏠  Dashboard");
        Button btnWorkers = sidebarBtn("🔍  Browse Workers");
        Button btnBookings = sidebarBtn("📋  My Bookings");
        Button btnProfile = sidebarBtn("👤  My Profile");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = sidebarBtn("🚪  Logout");
        btnLogout.getStyleClass().add("btn-danger");
        btnLogout.setStyle(
                "-fx-background-color:transparent; -fx-text-fill:#F44336; -fx-font-size:14px; -fx-padding:14 24; -fx-alignment:CENTER-LEFT;");

        sidebar.getChildren().addAll(brand, userName, sep,
                btnHome, btnWorkers, btnBookings, btnProfile, spacer, btnLogout);

        // ── Content ──
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));

        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:transparent; -fx-background-color:transparent;");

        root.setLeft(sidebar);
        root.setCenter(scroll);

        // ── Actions ──
        btnHome.setOnAction(e -> showDashboard());
        btnWorkers.setOnAction(e -> showBrowseWorkers());
        btnBookings.setOnAction(e -> showBookings());
        btnProfile.setOnAction(e -> showProfile());
        btnLogout.setOnAction(e -> {
            ctrl.logout();
            app.navigateTo(createWelcomeRedirect());
        });

        highlightBtn(btnHome);
        showDashboard();
        return root;
    }

    // ═══════════════ DASHBOARD HOME ═══════════════

    private void showDashboard() {
        contentArea.getChildren().clear();
        Label heading = styledHeading("📊  Dashboard Overview");

        HBox stats = new HBox(20);
        stats.getChildren().addAll(
                statCard("🛠", String.valueOf(ctrl.getWorkers().size()), "Workers Available"),
                statCard("📋", String.valueOf(ctrl.getBookingsForUser().size()), "My Bookings"),
                statCard("⭐", getAvgRating(), "Avg Worker Rating"),
                statCard("✅", String.valueOf(countByStatus(BookingStatus.COMPLETED)), "Completed Jobs"));

        Label recentTitle = styledHeading("📋  Recent Bookings");
        VBox recentList = new VBox(10);
        List<Booking> bookings = ctrl.getBookingsForUser();
        if (bookings.isEmpty()) {
            Label empty = new Label("No bookings yet. Browse workers to get started!");
            empty.getStyleClass().add("subtitle-text");
            recentList.getChildren().add(empty);
        } else {
            int limit = Math.min(3, bookings.size());
            for (int i = bookings.size() - 1; i >= bookings.size() - limit; i--) {
                recentList.getChildren().add(bookingCard(bookings.get(i)));
            }
        }

        contentArea.getChildren().addAll(heading, stats, recentTitle, recentList);
        animateContent();
    }

    // ═══════════════ BROWSE WORKERS ═══════════════

    private void showBrowseWorkers() {
        contentArea.getChildren().clear();
        Label heading = styledHeading("🔍  Browse Workers");

        HBox filters = new HBox(12);
        filters.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or city...");
        searchField.setPrefWidth(260);

        ComboBox<String> skillFilter = new ComboBox<>();
        skillFilter.getItems().add("All Skills");
        for (Skill s : Skill.values())
            skillFilter.getItems().add(s.name());
        skillFilter.setValue("All Skills");

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("btn-primary");

        filters.getChildren().addAll(searchField, skillFilter, searchBtn);

        VBox workersList = new VBox(12);

        Runnable doSearch = () -> {
            Skill sk = skillFilter.getValue().equals("All Skills") ? null : Skill.valueOf(skillFilter.getValue());
            List<Worker> results = ctrl.searchWorkers(searchField.getText(), sk);
            workersList.getChildren().clear();
            if (results.isEmpty()) {
                Label none = new Label("No workers found matching your criteria.");
                none.getStyleClass().add("subtitle-text");
                workersList.getChildren().add(none);
            } else {
                for (Worker w : results) {
                    workersList.getChildren().add(workerCard(w));
                }
            }
        };

        searchBtn.setOnAction(e -> doSearch.run());
        doSearch.run();

        contentArea.getChildren().addAll(heading, filters, workersList);
        animateContent();
    }

    private HBox workerCard(Worker w) {
        HBox card = new HBox(16);
        card.getStyleClass().add("worker-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox avatar = new VBox();
        avatar.setAlignment(Pos.CENTER);
        avatar.setPrefSize(52, 52);
        avatar.setStyle("-fx-background-color:rgba(255,107,53,0.15); -fx-background-radius:50;");
        Label initials = new Label(w.getName().substring(0, 1).toUpperCase());
        initials.setStyle("-fx-text-fill:#FF6B35; -fx-font-size:22px; -fx-font-weight:bold;");
        avatar.getChildren().add(initials);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(w.getName());
        name.setStyle("-fx-text-fill:white; -fx-font-size:16px; -fx-font-weight:bold;");

        HBox badges = new HBox(8);
        Label skillBadge = new Label(w.getSkill().name());
        skillBadge.getStyleClass().add("skill-badge");
        Label loc = new Label("📍 " + w.getLocation());
        loc.setStyle("-fx-text-fill:#8888AA; -fx-font-size:12px;");
        badges.getChildren().addAll(skillBadge, loc);

        Label stars = new Label(MainApp.getStarString(w.getRating()));
        stars.getStyleClass().add("star-rating");

        Label avail = new Label("● " + w.getAvailability().name());
        String avColor = w.getAvailability() == Availability.AVAILABLE ? "#4CAF50"
                : w.getAvailability() == Availability.BUSY ? "#FFC107" : "#F44336";
        avail.setStyle("-fx-text-fill:" + avColor + "; -fx-font-size:12px; -fx-font-weight:bold;");

        info.getChildren().addAll(name, badges, new HBox(12, stars, avail));

        Button bookBtn = new Button("Book Now");
        bookBtn.getStyleClass().add("btn-primary");
        bookBtn.setStyle("-fx-font-size:13px; -fx-padding:8 18;");
        bookBtn.setOnAction(e -> showBookingForm(w));

        card.getChildren().addAll(avatar, info, bookBtn);
        return card;
    }

    // ═══════════════ BOOKING FORM ═══════════════

    private void showBookingForm(Worker w) {
        contentArea.getChildren().clear();
        Label heading = styledHeading("📝  Book " + w.getName());

        VBox formCard = new VBox(14);
        formCard.getStyleClass().add("card");
        formCard.setMaxWidth(500);

        Label workerInfo = new Label(w.getName() + " • " + w.getSkill() + " • " + MainApp.getStarString(w.getRating()));
        workerInfo.setStyle("-fx-text-fill:#CCCCDD; -fx-font-size:14px;");

        TextArea descField = new TextArea();
        descField.setPromptText("Describe the work you need done...");
        descField.setPrefRowCount(3);

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));

        Label errorLbl = new Label();
        errorLbl.getStyleClass().add("error-text");
        errorLbl.setVisible(false);

        Button submitBtn = new Button("Confirm Booking");
        submitBtn.getStyleClass().add("btn-success");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        submitBtn.setOnAction(e -> {
            if (descField.getText().trim().isEmpty()) {
                errorLbl.setText("Please describe the work needed.");
                errorLbl.setVisible(true);
                return;
            }
            if (datePicker.getValue() == null || datePicker.getValue().isBefore(LocalDate.now())) {
                errorLbl.setText("Please select a valid future date.");
                errorLbl.setVisible(true);
                return;
            }
            ctrl.createBooking(w, descField.getText(), datePicker.getValue());
            showBookingSuccess(w);
        });

        Button cancelBtn = new Button("← Cancel");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> showBrowseWorkers());

        formCard.getChildren().addAll(
                new Label("Worker") {
                    {
                        getStyleClass().add("subtitle-text");
                    }
                }, workerInfo,
                new Separator(),
                new Label("Description") {
                    {
                        getStyleClass().add("subtitle-text");
                    }
                }, descField,
                new Label("Preferred Date") {
                    {
                        getStyleClass().add("subtitle-text");
                    }
                }, datePicker,
                errorLbl, submitBtn);

        contentArea.getChildren().addAll(heading, formCard, cancelBtn);
        animateContent();
    }

    private void showBookingSuccess(Worker w) {
        contentArea.getChildren().clear();
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(60));

        Label icon = new Label("✅");
        icon.setStyle("-fx-font-size:64px;");
        Label msg = new Label("Booking Confirmed!");
        msg.setStyle("-fx-text-fill:white; -fx-font-size:26px; -fx-font-weight:bold;");
        Label sub = new Label("Your booking with " + w.getName() + " has been placed.\nThey will be notified shortly.");
        sub.getStyleClass().add("subtitle-text");
        sub.setTextAlignment(TextAlignment.CENTER);

        Button viewBtn = new Button("View My Bookings");
        viewBtn.getStyleClass().add("btn-primary");
        viewBtn.setOnAction(e -> showBookings());

        box.getChildren().addAll(icon, msg, sub, viewBtn);
        contentArea.getChildren().add(box);
        animateContent();
    }

    // ═══════════════ MY BOOKINGS ═══════════════

    private void showBookings() {
        contentArea.getChildren().clear();
        Label heading = styledHeading("📋  My Bookings");

        VBox list = new VBox(12);
        List<Booking> bookings = ctrl.getBookingsForUser();
        if (bookings.isEmpty()) {
            Label empty = new Label("You haven't made any bookings yet.");
            empty.getStyleClass().add("subtitle-text");
            list.getChildren().add(empty);
        } else {
            for (int i = bookings.size() - 1; i >= 0; i--) {
                list.getChildren().add(bookingCard(bookings.get(i)));
            }
        }
        contentArea.getChildren().addAll(heading, list);
        animateContent();
    }

    private HBox bookingCard(Booking b) {
        HBox card = new HBox(14);
        card.getStyleClass().add("worker-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label title = new Label("Booking #" + b.getId() + " — " + b.getWorker().getName());
        title.setStyle("-fx-text-fill:white; -fx-font-size:15px; -fx-font-weight:bold;");

        Label detail = new Label(b.getWorker().getSkill() + " • " + b.getScheduledDate());
        detail.setStyle("-fx-text-fill:#8888AA; -fx-font-size:13px;");

        Label desc = new Label(b.getDescription());
        desc.setStyle("-fx-text-fill:#AAAACC; -fx-font-size:12px;");

        info.getChildren().addAll(title, detail, desc);

        Label status = new Label(b.getStatus().name());
        String color = switch (b.getStatus()) {
            case PENDING -> "#FFC107";
            case ACCEPTED -> "#2196F3";
            case IN_PROGRESS -> "#FF9800";
            case COMPLETED -> "#4CAF50";
            case CANCELLED -> "#F44336";
        };
        status.setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold; -fx-font-size:13px;");

        if (b.getStatus() == BookingStatus.COMPLETED && !b.isRated()) {
            Button rateBtn = new Button("Rate Worker");
            rateBtn.getStyleClass().add("btn-primary");
            rateBtn.setStyle("-fx-font-size:12px; -fx-padding:8 14;");
            rateBtn.setOnAction(e -> showRatingForm(b));
            card.getChildren().addAll(info, status, rateBtn);
        } else if (b.getStatus() == BookingStatus.COMPLETED && b.isRated()) {
            Label ratedLabel = new Label("✓ Rated");
            ratedLabel.setStyle("-fx-text-fill:#4CAF50; -fx-font-size:12px; -fx-font-weight:bold;");
            card.getChildren().addAll(info, status, ratedLabel);
        } else {
            card.getChildren().addAll(info, status);
        }
        return card;
    }

    private void showRatingForm(Booking booking) {
        contentArea.getChildren().clear();
        Label heading = styledHeading("⭐ Rate " + booking.getWorker().getName());

        VBox formCard = new VBox(14);
        formCard.getStyleClass().add("card");
        formCard.setMaxWidth(520);

        Label workerInfo = new Label(
                "Worker: " + booking.getWorker().getName() + " • " + booking.getWorker().getSkill());
        workerInfo.setStyle("-fx-text-fill:#CCCCDD; -fx-font-size:14px;");

        Label instructions = new Label("Choose a rating between 0 and 5 stars.");
        instructions.getStyleClass().add("subtitle-text");

        ComboBox<String> ratingSelect = new ComboBox<>();
        for (int i = 0; i <= 10; i++) {
            ratingSelect.getItems().add(String.format("%.1f", i * 0.5));
        }
        ratingSelect.setValue("5.0");
        ratingSelect.setPrefWidth(120);

        Label errorLbl = new Label();
        errorLbl.getStyleClass().add("error-text");
        errorLbl.setVisible(false);

        Button submitBtn = new Button("Submit Rating");
        submitBtn.getStyleClass().add("btn-success");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setOnAction(e -> {
            try {
                double rating = Double.parseDouble(ratingSelect.getValue());
                if (rating < 0 || rating > 5) {
                    throw new NumberFormatException();
                }
                ctrl.rateBooking(booking, rating);
                showBookings();
            } catch (NumberFormatException ex) {
                errorLbl.setText("Please select a valid rating between 0 and 5.");
                errorLbl.setVisible(true);
            }
        });

        Button cancelBtn = new Button("← Back to Bookings");
        cancelBtn.getStyleClass().add("btn-secondary");
        cancelBtn.setOnAction(e -> showBookings());

        formCard.getChildren().addAll(
                workerInfo,
                instructions,
                ratingSelect,
                errorLbl,
                submitBtn);

        contentArea.getChildren().addAll(heading, formCard, cancelBtn);
        animateContent();
    }

    // ═══════════════ MY PROFILE ═══════════════

    private void showProfile() {
        contentArea.getChildren().clear();
        User u = ctrl.getCurrentUser();
        Label heading = styledHeading("👤  My Profile");

        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setMaxWidth(460);

        card.getChildren().addAll(
                profileRow("Name", u.getName()),
                profileRow("Email", u.getEmail()),
                profileRow("Phone", u.getPhoneNo()),
                profileRow("Location", u.getLocation()),
                profileRow("Total Bookings", String.valueOf(ctrl.getBookingsForUser().size())));

        contentArea.getChildren().addAll(heading, card);
        animateContent();
    }

    private HBox profileRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-text-fill:#8888AA; -fx-font-size:14px; -fx-min-width:120;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:white; -fx-font-size:15px; -fx-font-weight:bold;");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    // ═══════════════ HELPERS ═══════════════

    private Pane createWelcomeRedirect() {
        // Simple hack: access MainApp's welcome by re-launching from scratch
        VBox dummy = new VBox();
        javafx.application.Platform.runLater(() -> {
            try {
                MainApp newApp = new MainApp();
                newApp.start((javafx.stage.Stage) contentArea.getScene().getWindow());
            } catch (Exception ignored) {
            }
        });
        return dummy;
    }

    private Button sidebarBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void highlightBtn(Button btn) {
        btn.getStyleClass().add("sidebar-btn-active");
    }

    private Label styledHeading(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("heading-text");
        return l;
    }

    private VBox statCard(String icon, String value, String label) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER_LEFT);

        Label ic = new Label(icon);
        ic.setStyle("-fx-font-size:24px;");
        Label val = new Label(value);
        val.getStyleClass().add("stat-value");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");

        card.getChildren().addAll(ic, val, lbl);
        return card;
    }

    private String getAvgRating() {
        double avg = ctrl.getWorkers().stream().mapToDouble(Worker::getRating).average().orElse(0);
        return String.format("%.1f", avg);
    }

    private long countByStatus(BookingStatus status) {
        return ctrl.getBookingsForUser().stream().filter(b -> b.getStatus() == status).count();
    }

    private void animateContent() {
        for (int i = 0; i < contentArea.getChildren().size(); i++) {
            javafx.scene.Node n = contentArea.getChildren().get(i);
            n.setOpacity(0);
            n.setTranslateY(20);
            FadeTransition ft = new FadeTransition(Duration.millis(350), n);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(Duration.millis(i * 60));
            TranslateTransition tt = new TranslateTransition(Duration.millis(350), n);
            tt.setFromY(20);
            tt.setToY(0);
            tt.setDelay(Duration.millis(i * 60));
            tt.setInterpolator(Interpolator.EASE_OUT);
            ft.play();
            tt.play();
        }
    }
}

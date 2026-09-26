package com.fixit.view;

import com.fixit.MainApp;
import com.fixit.controller.AppController;
import com.fixit.model.*;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.List;

public class WorkerDashboardView {

    private final MainApp app;
    private final AppController ctrl;
    private VBox contentArea;

    public WorkerDashboardView(MainApp app, AppController ctrl) {
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

        Label workerName = new Label("Hello, " + ctrl.getCurrentWorker().getName());
        workerName.setStyle("-fx-text-fill:#CCCCDD; -fx-font-size:13px; -fx-padding: 0 24 16 24;");

        Separator sep = new Separator();

        Button btnHome = sidebarBtn("🏠  Dashboard");
        Button btnJobs = sidebarBtn("📋  Job Requests");
        Button btnAvail = sidebarBtn("🟢  Availability");
        Button btnProfile = sidebarBtn("👤  My Profile");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("🚪  Logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle("-fx-background-color:transparent; -fx-text-fill:#F44336; -fx-font-size:14px; -fx-padding:14 24; -fx-alignment:CENTER-LEFT; -fx-cursor:hand;");

        sidebar.getChildren().addAll(brand, workerName, sep,
                btnHome, btnJobs, btnAvail, btnProfile, spacer, btnLogout);

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
        btnJobs.setOnAction(e -> showJobRequests());
        btnAvail.setOnAction(e -> showAvailability());
        btnProfile.setOnAction(e -> showProfile());
        btnLogout.setOnAction(e -> {
            ctrl.logout();
            javafx.application.Platform.runLater(() -> {
                try {
                    MainApp newApp = new MainApp();
                    newApp.start((javafx.stage.Stage) contentArea.getScene().getWindow());
                } catch (Exception ignored) {}
            });
        });

        btnHome.getStyleClass().add("sidebar-btn-active");
        showDashboard();
        return root;
    }

    // ═══════════════ DASHBOARD ═══════════════

    private void showDashboard() {
        contentArea.getChildren().clear();
        Worker w = ctrl.getCurrentWorker();

        Label heading = styledHeading("📊  Worker Dashboard");

        HBox stats = new HBox(20);
        List<Booking> myJobs = ctrl.getBookingsForWorker();
        long pending = myJobs.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count();
        long completed = myJobs.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();

        stats.getChildren().addAll(
            statCard("⭐", MainApp.getStarString(w.getRating()).split(" ")[1], "My Rating"),
            statCard("📋", String.valueOf(myJobs.size()), "Total Jobs"),
            statCard("⏳", String.valueOf(pending), "Pending"),
            statCard("✅", String.valueOf(completed), "Completed")
        );

        // Quick status
        HBox statusBox = new HBox(12);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.getStyleClass().add("stat-card");
        statusBox.setMaxWidth(460);

        Label statusLbl = new Label("Current Status: ");
        statusLbl.setStyle("-fx-text-fill:#8888AA; -fx-font-size:15px;");

        String avColor = w.getAvailability() == Availability.AVAILABLE ? "#4CAF50" :
                          w.getAvailability() == Availability.BUSY ? "#FFC107" : "#F44336";
        Label statusVal = new Label("● " + w.getAvailability().name());
        statusVal.setStyle("-fx-text-fill:" + avColor + "; -fx-font-size:16px; -fx-font-weight:bold;");

        statusBox.getChildren().addAll(statusLbl, statusVal);

        // Recent jobs
        Label recentTitle = styledHeading("📋  Recent Job Requests");
        VBox recentList = new VBox(10);
        if (myJobs.isEmpty()) {
            Label empty = new Label("No job requests yet. Keep your profile active!");
            empty.getStyleClass().add("subtitle-text");
            recentList.getChildren().add(empty);
        } else {
            int limit = Math.min(3, myJobs.size());
            for (int i = myJobs.size() - 1; i >= myJobs.size() - limit; i--) {
                recentList.getChildren().add(jobCard(myJobs.get(i)));
            }
        }

        contentArea.getChildren().addAll(heading, stats, statusBox, recentTitle, recentList);
        animateContent();
    }

    // ═══════════════ JOB REQUESTS ═══════════════

    private void showJobRequests() {
        contentArea.getChildren().clear();
        Label heading = styledHeading("📋  Job Requests");

        VBox list = new VBox(12);
        List<Booking> jobs = ctrl.getBookingsForWorker();
        if (jobs.isEmpty()) {
            Label empty = new Label("No job requests yet.");
            empty.getStyleClass().add("subtitle-text");
            list.getChildren().add(empty);
        } else {
            for (int i = jobs.size() - 1; i >= 0; i--) {
                list.getChildren().add(jobCard(jobs.get(i)));
            }
        }

        contentArea.getChildren().addAll(heading, list);
        animateContent();
    }

    private HBox jobCard(Booking b) {
        HBox card = new HBox(14);
        card.getStyleClass().add("worker-card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label title = new Label("Job #" + b.getId() + " — " + b.getClient().getName());
        title.setStyle("-fx-text-fill:white; -fx-font-size:15px; -fx-font-weight:bold;");

        Label detail = new Label("📅 " + b.getScheduledDate() + " • 📍 " + b.getClient().getLocation());
        detail.setStyle("-fx-text-fill:#8888AA; -fx-font-size:13px;");

        Label desc = new Label(b.getDescription());
        desc.setStyle("-fx-text-fill:#AAAACC; -fx-font-size:12px;");
        desc.setWrapText(true);

        info.getChildren().addAll(title, detail, desc);

        VBox actions = new VBox(6);
        actions.setAlignment(Pos.CENTER);

        Label status = new Label(b.getStatus().name());
        String color = switch (b.getStatus()) {
            case PENDING -> "#FFC107";
            case ACCEPTED -> "#2196F3";
            case IN_PROGRESS -> "#FF9800";
            case COMPLETED -> "#4CAF50";
            case CANCELLED -> "#F44336";
        };
        status.setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold; -fx-font-size:12px;");

        if (b.getStatus() == BookingStatus.PENDING) {
            Button acceptBtn = new Button("Accept");
            acceptBtn.getStyleClass().add("btn-success");
            acceptBtn.setStyle("-fx-font-size:12px; -fx-padding:6 14;");
            acceptBtn.setOnAction(e -> {
                b.setStatus(BookingStatus.ACCEPTED);
                showJobRequests();
            });
            actions.getChildren().addAll(status, acceptBtn);
        } else if (b.getStatus() == BookingStatus.ACCEPTED) {
            Button startBtn = new Button("Start Work");
            startBtn.getStyleClass().add("btn-primary");
            startBtn.setStyle("-fx-font-size:12px; -fx-padding:6 14;");
            startBtn.setOnAction(e -> {
                b.setStatus(BookingStatus.IN_PROGRESS);
                showJobRequests();
            });
            actions.getChildren().addAll(status, startBtn);
        } else if (b.getStatus() == BookingStatus.IN_PROGRESS) {
            Button doneBtn = new Button("Complete");
            doneBtn.getStyleClass().add("btn-success");
            doneBtn.setStyle("-fx-font-size:12px; -fx-padding:6 14;");
            doneBtn.setOnAction(e -> {
                b.setStatus(BookingStatus.COMPLETED);
                showJobRequests();
            });
            actions.getChildren().addAll(status, doneBtn);
        } else {
            actions.getChildren().add(status);
        }

        card.getChildren().addAll(info, actions);
        return card;
    }

    // ═══════════════ AVAILABILITY ═══════════════

    private void showAvailability() {
        contentArea.getChildren().clear();
        Worker w = ctrl.getCurrentWorker();
        Label heading = styledHeading("🟢  Update Availability");

        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(420);

        Label current = new Label("Current: " + w.getAvailability().name());
        current.setStyle("-fx-text-fill:white; -fx-font-size:16px;");

        ToggleGroup group = new ToggleGroup();
        VBox options = new VBox(10);
        for (Availability a : Availability.values()) {
            RadioButton rb = new RadioButton(a.name());
            rb.setToggleGroup(group);
            rb.setStyle("-fx-text-fill:#CCCCDD; -fx-font-size:14px;");
            rb.setUserData(a);
            if (a == w.getAvailability()) rb.setSelected(true);
            options.getChildren().add(rb);
        }

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            Toggle sel = group.getSelectedToggle();
            if (sel != null) {
                w.setAvailability((Availability) sel.getUserData());
                current.setText("Current: " + w.getAvailability().name());
                showAlert("Updated", "Availability set to " + w.getAvailability().name());
            }
        });

        card.getChildren().addAll(current, new Separator(), options, saveBtn);
        contentArea.getChildren().addAll(heading, card);
        animateContent();
    }

    // ═══════════════ PROFILE ═══════════════

    private void showProfile() {
        contentArea.getChildren().clear();
        Worker w = ctrl.getCurrentWorker();
        Label heading = styledHeading("👤  My Profile");

        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setMaxWidth(460);

        card.getChildren().addAll(
            profileRow("Name", w.getName()),
            profileRow("Email", w.getEmail()),
            profileRow("Phone", w.getPhoneNo()),
            profileRow("Location", w.getLocation()),
            profileRow("Skill", w.getSkill().name()),
            profileRow("Rating", MainApp.getStarString(w.getRating())),
            profileRow("Status", w.getAvailability().name()),
            profileRow("Total Jobs", String.valueOf(ctrl.getBookingsForWorker().size()))
        );

        contentArea.getChildren().addAll(heading, card);
        animateContent();
    }

    // ═══════════════ HELPERS ═══════════════

    private Button sidebarBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private Label styledHeading(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("heading-text");
        return l;
    }

    private VBox statCard(String icon, String value, String label) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(170);
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

    private HBox profileRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-text-fill:#8888AA; -fx-font-size:14px; -fx-min-width:110;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:white; -fx-font-size:15px; -fx-font-weight:bold;");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().getStylesheets().add(getClass().getResource("/com/fixit/styles.css").toExternalForm());
        a.getDialogPane().getStyleClass().add("dialog-pane");
        a.showAndWait();
    }

    private void animateContent() {
        for (int i = 0; i < contentArea.getChildren().size(); i++) {
            javafx.scene.Node n = contentArea.getChildren().get(i);
            n.setOpacity(0); n.setTranslateY(20);
            FadeTransition ft = new FadeTransition(Duration.millis(350), n);
            ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.millis(i * 60));
            TranslateTransition tt = new TranslateTransition(Duration.millis(350), n);
            tt.setFromY(20); tt.setToY(0); tt.setDelay(Duration.millis(i * 60));
            tt.setInterpolator(Interpolator.EASE_OUT);
            ft.play(); tt.play();
        }
    }
}

package com.fixit;

import com.fixit.controller.AppController;
import com.fixit.model.*;
import com.fixit.view.UserDashboardView;
import com.fixit.view.WorkerDashboardView;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    private Stage stage;
    private StackPane rootContainer;
    private Scene mainScene;
    private AppController controller;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.controller = AppController.getInstance();

        rootContainer = new StackPane();
        rootContainer.getStyleClass().add("dark-bg");

        mainScene = new Scene(rootContainer, 1100, 720);
        mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        mainScene.setFill(Color.web("#1B1B2F"));

        stage.setScene(mainScene);
        stage.setTitle("FixIt — Find Trusted Workers Near You");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        navigateTo(createWelcomeView());
        stage.show();
    }

    public void navigateTo(Pane newContent) {
        newContent.getStyleClass().add("dark-bg");
        if (rootContainer.getChildren().isEmpty()) {
            rootContainer.getChildren().add(newContent);
            FadeTransition ft = new FadeTransition(Duration.millis(400), newContent);
            ft.setFromValue(0); ft.setToValue(1); ft.play();
        } else {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootContainer);
            fadeOut.setFromValue(1); fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                rootContainer.getChildren().setAll(newContent);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(250), rootContainer);
                fadeIn.setFromValue(0); fadeIn.setToValue(1); fadeIn.play();
            });
            fadeOut.play();
        }
    }

    // ═══════════════════ WELCOME SCREEN ═══════════════════

    private Pane createWelcomeView() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        // Brand
        Label logo = new Label("🔧 FixIt");
        logo.setStyle("-fx-font-size:52px; -fx-font-weight:bold; -fx-text-fill:#FF6B35;");

        Label tagline = new Label("Find Trusted Workers Near You");
        tagline.getStyleClass().add("subtitle-text");
        tagline.setStyle("-fx-font-size:18px;");

        Region spacer = new Region();
        spacer.setPrefHeight(30);

        // Role cards
        HBox cards = new HBox(30);
        cards.setAlignment(Pos.CENTER);

        VBox clientCard = createRoleCard("👤", "I Need Help", "Find verified electricians, plumbers,\ncarpenters & more near you.");
        VBox workerCard = createRoleCard("🛠", "I'm a Worker", "List your services, accept jobs,\nand grow your business.");

        clientCard.setOnMouseClicked(e -> navigateTo(createLoginView(true)));
        workerCard.setOnMouseClicked(e -> navigateTo(createLoginView(false)));

        cards.getChildren().addAll(clientCard, workerCard);

        Label footer = new Label("Pakistan's #1 Skilled Worker Marketplace");
        footer.setStyle("-fx-text-fill:#555577; -fx-font-size:12px;");

        root.getChildren().addAll(logo, tagline, spacer, cards, footer);

        // Animate cards
        animateSlideUp(clientCard, 0);
        animateSlideUp(workerCard, 100);

        return root;
    }

    private VBox createRoleCard(String icon, String title, String desc) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("role-card");
        card.setPrefSize(280, 220);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size:48px;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill:white; -fx-font-size:20px; -fx-font-weight:bold;");

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill:#8888AA; -fx-font-size:13px; -fx-text-alignment:center;");
        descLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        card.getChildren().addAll(iconLbl, titleLbl, descLbl);
        return card;
    }

    // ═══════════════════ LOGIN SCREEN ═══════════════════

    private Pane createLoginView(boolean isUser) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");
        card.setMaxWidth(420);
        card.setPrefWidth(420);

        Label title = new Label(isUser ? "👤  Client Login" : "🛠  Worker Login");
        title.setStyle("-fx-text-fill:white; -fx-font-size:24px; -fx-font-weight:bold;");

        Label subtitle = new Label("Welcome back! Enter your credentials.");
        subtitle.getStyleClass().add("subtitle-text");

        TextField emailField = new TextField();
        emailField.setPromptText("Email address");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setVisible(false);

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            if (isUser) {
                User u = controller.loginUser(emailField.getText(), passField.getText());
                if (u != null) {
                    navigateTo(new UserDashboardView(this, controller).createView());
                } else {
                    errorLabel.setText("Invalid email or password.");
                    errorLabel.setVisible(true);
                }
            } else {
                Worker w = controller.loginWorker(emailField.getText(), passField.getText());
                if (w != null) {
                    navigateTo(new WorkerDashboardView(this, controller).createView());
                } else {
                    errorLabel.setText("Invalid email or password.");
                    errorLabel.setVisible(true);
                }
            }
        });

        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setStyle("-fx-text-fill:#FF6B35; -fx-font-size:13px;");
        registerLink.setOnAction(e -> navigateTo(createRegisterView(isUser)));

        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> navigateTo(createWelcomeView()));

        card.getChildren().addAll(title, subtitle, new Region(){{ setPrefHeight(8); }},
                emailField, passField, errorLabel, loginBtn, registerLink);

        VBox wrapper = new VBox(16, card, backBtn);
        wrapper.setAlignment(Pos.CENTER);
        root.getChildren().add(wrapper);

        animateSlideUp(card, 0);
        return root;
    }

    // ═══════════════════ REGISTER SCREEN ═══════════════════

    private Pane createRegisterView(boolean isUser) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("card");
        card.setMaxWidth(440);
        card.setPrefWidth(440);

        Label title = new Label(isUser ? "👤  Create Client Account" : "🛠  Create Worker Account");
        title.setStyle("-fx-text-fill:white; -fx-font-size:22px; -fx-font-weight:bold;");

        TextField nameField = new TextField();  nameField.setPromptText("Full Name");
        TextField emailField = new TextField(); emailField.setPromptText("Email Address");
        TextField phoneField = new TextField(); phoneField.setPromptText("Phone (11 digits)");
        TextField locField = new TextField();   locField.setPromptText("City / Location");
        PasswordField passField = new PasswordField(); passField.setPromptText("Password (min 6 chars)");

        ComboBox<Skill> skillBox = null;
        if (!isUser) {
            skillBox = new ComboBox<>();
            skillBox.getItems().setAll(Skill.values());
            skillBox.setPromptText("Select Your Skill");
            skillBox.setMaxWidth(Double.MAX_VALUE);
        }

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-text");
        errorLabel.setVisible(false);

        Label successLabel = new Label();
        successLabel.getStyleClass().add("success-text");
        successLabel.setVisible(false);

        Button regBtn = new Button("Create Account");
        regBtn.getStyleClass().add("btn-primary");
        regBtn.setMaxWidth(Double.MAX_VALUE);

        final ComboBox<Skill> finalSkillBox = skillBox;
        regBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            successLabel.setVisible(false);
            String err;
            if (isUser) {
                err = controller.registerUser(nameField.getText(), emailField.getText(),
                        phoneField.getText(), locField.getText(), passField.getText());
            } else {
                Skill sk = finalSkillBox != null ? finalSkillBox.getValue() : null;
                err = controller.registerWorker(nameField.getText(), emailField.getText(),
                        phoneField.getText(), locField.getText(), passField.getText(), sk);
            }
            if (err != null) {
                errorLabel.setText(err);
                errorLabel.setVisible(true);
            } else {
                successLabel.setText("Account created! Redirecting to login...");
                successLabel.setVisible(true);
                PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
                pause.setOnFinished(ev -> navigateTo(createLoginView(isUser)));
                pause.play();
            }
        });

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill:#FF6B35; -fx-font-size:13px;");
        loginLink.setOnAction(e -> navigateTo(createLoginView(isUser)));

        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> navigateTo(createWelcomeView()));

        card.getChildren().addAll(title, new Region(){{ setPrefHeight(4); }},
                nameField, emailField, phoneField, locField, passField);
        if (!isUser) card.getChildren().add(finalSkillBox);
        card.getChildren().addAll(errorLabel, successLabel, regBtn, loginLink);

        ScrollPane sp = new ScrollPane(new VBox(16, card, backBtn){{ setAlignment(Pos.CENTER); }});
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:transparent; -fx-background-color:transparent;");

        root.getChildren().add(sp);
        VBox.setVgrow(sp, Priority.ALWAYS);

        animateSlideUp(card, 0);
        return root;
    }

    // ═══════════════════ UTILITIES ═══════════════════

    private void animateSlideUp(javafx.scene.Node node, int delayMs) {
        node.setTranslateY(30);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(450), node);
        tt.setFromY(30); tt.setToY(0); tt.setDelay(Duration.millis(delayMs));
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(450), node);
        ft.setFromValue(0); ft.setToValue(1); ft.setDelay(Duration.millis(delayMs));
        tt.play(); ft.play();
    }

    public static String getStarString(double rating) {
        StringBuilder sb = new StringBuilder();
        int full = (int) rating;
        for (int i = 0; i < full; i++) sb.append("★");
        for (int i = full; i < 5; i++) sb.append("☆");
        return sb.toString() + " " + String.format("%.1f", rating);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

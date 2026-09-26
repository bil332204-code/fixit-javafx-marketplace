# FixIt — Skilled Worker Marketplace

FixIt is a **JavaFX desktop marketplace** for connecting clients with skilled workers such as electricians, plumbers, carpenters, painters, car mechanics, and bike mechanics. The application was developed as an Object-Oriented Programming project at **NUST SEECS**.

It demonstrates a complete desktop application flow: registration and login, worker discovery, booking management, worker-side job progression, ratings, separate dashboards, and local file-based persistence.

## Key Features

- Separate **Client** and **Worker** registration/login flows
- Search workers by **name, city, and skill category**
- Six supported skilled-trade categories
- Create bookings with a job description and scheduled date
- Booking lifecycle: `PENDING → ACCEPTED → IN_PROGRESS → COMPLETED → CANCELLED`
- Worker availability states: `AVAILABLE`, `BUSY`, `OFFLINE`, `ONLINE`
- Client rating flow from **0 to 5 stars** after job completion
- Separate Client and Worker dashboards
- File-based persistence for users, workers, and bookings
- Custom JavaFX CSS, cards, sidebar navigation, and screen-transition animations

## OOP Design

The project applies the main object-oriented concepts taught in the course:

- **Abstraction** — `Person` is an abstract base class with abstract `login()`, `logout()`, and `viewProfile()` methods.
- **Inheritance** — `User` and `Worker` extend `Person`.
- **Encapsulation** — model state is accessed through getters/setters and validated methods.
- **Polymorphism** — `User` and `Worker` provide their own implementations of inherited abstract behavior.
- **Singleton pattern** — `AppController.getInstance()` provides a single application controller coordinating UI, session state, data, and bookings.

## Architecture

```text
JavaFX UI
  ├── MainApp
  ├── UserDashboardView
  └── WorkerDashboardView
        │
        ▼
AppController (Singleton)
        │
        ├── Users
        ├── Workers
        ├── Bookings
        └── Session state
        │
        ▼
DataStore
        │
        └── Local pipe-delimited text files
```

### Project Structure

```text
src/main/java/com/fixit/
├── MainApp.java
├── controller/
│   ├── AppController.java
│   └── DataStore.java
├── model/
│   ├── Availability.java
│   ├── Booking.java
│   ├── BookingStatus.java
│   ├── Person.java
│   ├── Skill.java
│   ├── User.java
│   └── Worker.java
└── view/
    ├── UserDashboardView.java
    └── WorkerDashboardView.java

src/main/resources/com/fixit/
└── styles.css
```

## Technologies

- **Java 17**
- **JavaFX 17**
- **Maven**
- **CSS** for JavaFX styling
- Java file I/O with `BufferedReader`, `FileWriter`, and `PrintWriter`

## Run the Project

### Requirements

- JDK 17+
- Maven 3.8+

Clone the repository and run:

```bash
mvn clean javafx:run
```

On Windows, you can also run:

```text
run.bat
```

On macOS/Linux:

```bash
./run.sh
```

The application creates its local `data/` directory automatically on first run.

## Demo Accounts

On a fresh run, the application seeds demonstration data automatically.

**Client**

```text
Email: demo@example.com
Password: demo123
```

**Worker example**

```text
Email: worker1@example.com
Password: demo123
```

All demo contact details are fictional and included only for local testing.

## Persistence

`DataStore.java` stores local records as pipe-delimited text files:

```text
users.txt     → name | email | phone | location | password
workers.txt   → name | email | phone | location | password | skill | availability | rating
bookings.txt  → id | clientEmail | workerEmail | description | date | status | isRated
```

The runtime `data/` directory is intentionally excluded from Git so personal test records are not committed.

> **Security note:** this is an academic desktop prototype. Passwords are stored locally in plain text to demonstrate file persistence and should **not** be treated as production authentication. A production version should use password hashing, a proper database, authorization controls, and secure secret handling.

## Team & Contributions

This was a four-member Object-Oriented Programming project.

| Member | Primary contribution |
|---|---|
| **Bilal Ahmed — Group Leader** | JavaFX UI across the application, `AppController`, overall project architecture, full integration, build/run workflow |
| **Usman Ahmed** | Core OOP class design including `Person`, `User`, `Worker`, and `Booking` |
| **Riyan Khalid** | Method implementations, validation, and logic layer |
| **Anas Alam** | `DataStore.java`, file persistence, and storage formats |

As Group Leader, Bilal Ahmed was responsible for bringing the independently developed parts together into the final integrated desktop application.

## Limitations / Future Improvements

- Replace text-file persistence with SQLite/PostgreSQL or another database
- Hash passwords instead of storing plaintext credentials
- Add unit/integration tests
- Add profile images and richer worker portfolios
- Add booking notifications and messaging
- Improve rating aggregation to support review history instead of only the current stored rating
- Package the application as a distributable desktop installer

## Course Context

**Project:** FixIt — Skilled Worker Marketplace  
**Course:** Object-Oriented Programming  
**Institution:** NUST SEECS

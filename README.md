# Random Users app

### Summary

This mobile application consumes and presents user data from a remote API. Which is displayed in a infinte scrollable screen. The core design prioritizes a **resilient and scalable architecture** with a local caching strategy for enhanced performance. The project a for goal to demonstrate a strong command of modern Android development frameworks and established engineering principles.

### Commitment to Clean Architecture and SOLID Principles

The application's design is built on a foundation of **clean architecture** and **SOLID principles**, ensuring the codebase is modular, highly testable, and maintainable. A layered architecture separates the app into `UI`, `Domain`, and `Data` layers, directly applying the **Single Responsibility Principle (SRP)**. The **Dependency Inversion Principle (DIP)** is implemented via abstractions, ensuring high-level modules depend on interfaces rather than the low-level `Data` layer, a system managed by **Hilt**. This decoupling enables independent component testing and enhances resilience, as a repository pattern with a local data cache serves as a single source of truth during network instability.

### Technical Stack

* **Core Technologies:** The codebase is developed entirely in **Kotlin**, leveraging its native null safety, conciseness, and expressive syntax. The UI is built with **Jetpack Compose**, showcasing proficiency in declarative UI design. This modern approach simplifies UI development by managing UI state and rendering updates automatically. Asynchronous operations are handled with **Kotlin Coroutines** and **Flow**, providing a structured and efficient way to manage concurrent tasks and stream data reactively.

* **Data Handling:** The data layer utilizes **Retrofit** for consuming REST APIs and managing network communication. For local data persistence, **Room** is the chosen library, providing a robust, type-safe, and scalable SQLite abstraction layer. The seamless integration of these libraries with Coroutines and Flow simplifies complex data management tasks significantly.

### Future Work & Improvements

* **Project Modularity:** Refactor the single-module structure into a multi-module architecture to enable independent feature development, improve scalability, and reduce build times. This will also resolve test code duplication caused by limitations with **`testFixtures`** in a single-module project.

* **Comprehensive Integration Testing:** Implement end-to-end integration tests to validate the critical user flow from the list to the detail screen.

* **Localization:** Refactor hardcoded strings into Android resource files to prepare the application for internationalization.

* **Granular Error Handling:** Refine UI state management to provide explicit and user-friendly feedback for various error scenarios.

* **CI/CD Pipeline:** Integrate a Continuous Integration/Continuous Delivery pipeline to automate testing and build processes.

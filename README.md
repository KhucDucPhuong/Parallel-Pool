# Parallel-Pool Challenge

## 🎯 Overview

This repository contains a multithreaded Java Swing application that simulates a billiards game with dynamic ball interactions and demonstrates concurrent programming concepts. The project was developed as a challenge to manage shared resources and concurrency within a graphical user interface (GUI) environment.

## ✨ Key Features

* **Multithreaded Simulation:** Each billiard ball runs as an independent thread, allowing for real-time parallel movement and collision detection.
* **Dynamic Collision Detection:** Implements logic for detecting and resolving collisions between balls and the table boundaries.
* **Anti-Deadlock Resource Management:** Includes a mechanism (e.g., resource hierarchy or deadlock prevention strategy) to ensure the system remains live and avoids thread deadlock during complex resource acquisition scenarios.
* **Java Swing GUI:** A responsive graphical interface built with Java Swing to visualize the simulation.

## ⚙️ Technologies Used

* **Language:** Java
* **IDE:** NetBeans (Project structure may include `nbproject/` and `pom.xml`)
* **Concurrency:** Java `Thread` class, synchronization primitives (e.g., `synchronized`, `ReentrantLock` if used).
* **GUI:** Java Swing

## 🛠️ How to Run

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/KhucDucPhuong/Parallel-Pool.git](https://github.com/KhucDucPhuong/Parallel-Pool.git)
    ```
2.  **Open in NetBeans:** Open the project directory (`Parallel-Pool`) using the NetBeans IDE.
3.  **Run Main Class:** Locate and run the main application file (e.g., `MainApp.java` or `BilliardsApp.java`) within the `src/` directory.

## 🧑‍💻 Author

* **Author:** Khuc Duc Phuong
* **GitHub:** [@KhucDucPhuong](https://github.com/KhucDucPhuong)
* **YouTube:** [game Parallel-Pool](https://www.youtube.com/watch?v=UyuEu9K5qi8)

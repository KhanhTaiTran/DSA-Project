# 2048 Game

![Java](https://img.shields.io/badge/Language-Java-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

A polished, object-oriented implementation of the popular 2048 puzzle game built in Java. This project showcases clean architecture, SOLID principles, and modern software design patterns.

<p align="center">
  <!-- Consider adding a screenshot or GIF of your game here -->
  <em>2048 - Slide, combine, and reach 2048!</em>
</p>

## 🎮 About The Game

2048 is a single-player sliding block puzzle game where the goal is to combine numbered tiles to create a tile with the value 2048. The game is played on a 4×4 grid using the arrow keys to slide tiles in four directions. When two tiles with the same number touch, they merge into one tile with the sum of their values.

## ✨ Features

- **Core Gameplay**: Classic 2048 mechanics on a 4x4 grid
- **Undo System**: Revert your last move when you make a mistake
- **Smooth Animations**: Visually appealing tile movements and merges
- **AI Assistant**: Get move suggestions from the built-in AI
- **Clean Architecture**: Modular codebase following SOLID principles

## 🏗️ Architecture

The project is organized using a clean, layered architecture:

```
2048-game/
├── src/
│   ├── animation/         # Animation system for smooth tile movement
│   ├── Constants/         # Game configuration and constants
│   ├── game/              # Legacy game logic (targeted for refactoring)
│   ├── interfaces/        # Core interfaces defining the system behavior
│   ├── service/           # Business logic implementations
│   └── ui/                # Swing UI components and rendering logic
└── lib/                   # External dependencies
```

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Git (optional, for cloning the repository)

### Installation

1. **Clone the repository** (or download as ZIP):

   ```bash
   git clone https://github.com/KhanhTaiTran/DSA-Project.git
   cd 2048-game
   ```

2. **Compile the project**:

   ```bash
   javac -d bin src/**/*.java
   ```

3. **Run the game**:
   ```bash
   java -cp bin ui.GameBoard
   ```

## 🎯 How to Play

- **Arrow Keys Or A,S,D,W**: Move tiles in the respective direction
- **U Key**: Undo your last move
- **Restart Button**: Start a new game
- **AI Button**: Get a suggested move from the AI

## 🔧 Customization

The game can be customized in several ways:

- Modify grid dimensions in `Constants/Constants.java`
- Adjust scoring algorithm in service implementations
- Tweak AI behavior for different strategies
- Change tile colors and UI appearance in the UI package

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add an amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📜 License

This project is available for educational purposes. Feel free to use, modify, and learn from it.

---

<p align="center">
  Made with ❤️ as a Data Structures & Algorithms project
</p>

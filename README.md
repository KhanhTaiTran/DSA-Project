# 2048 Game (Java)

A modern, object-oriented implementation of the classic 2048 puzzle game in Java. This project demonstrates clean architecture and SOLID principles, featuring a responsive Swing UI, undo functionality, and an optional AI player.

## Features

- Classic 2048 gameplay on a 4x4 grid
- Undo last move functionality
- Animated tile movement for a smooth experience
- AI player for auto-move suggestions
- Clean, modular, and testable codebase

## Project Structure

```
2048-game/
├── src/
│   ├── animation/         # Tile animation classes
│   ├── Constants/         # Game constants (e.g., grid size)
│   ├── enums/             # Enums (e.g., Direction)
│   ├── game/              # Legacy logic (to be refactored)
│   ├── interfaces/        # Core interfaces (GameEngine, BoardService, etc.)
│   ├── model/             # Data models (Grid, Tile, GameState)
│   ├── service/           # Service implementations (BoardServiceImpl, etc.)
│   └── ui/                # Swing UI (GameBoard, WelcomePanel2048)
├── lib/                   # External libraries (if any)
└── README.md              # Project documentation
```

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher

### Build & Run

1. **Compile the project:**
   - Open a terminal in the project root directory.
   - Run:
     ```
     javac -d bin src/**/*.java
     ```
2. **Run the game:**
   - Run:
     ```
     java -cp bin ui.GameBoard
     ```

## Controls

- **Arrow Keys:** Move tiles
- **U:** Undo last move
- **Restart:** Click the Restart button
- **AI Move:** Click the AI button for a suggested move

## Customization

- Change grid size in `Constants/Constants.java`
- Tweak scoring or AI by editing service implementations

## Contributing

Contributions are welcome! Please open issues or submit pull requests for improvements.

---

_For educational use. Contributions welcome!_

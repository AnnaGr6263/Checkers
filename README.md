# Chinese Checkers
Project in pairs for software engineering course.  
Algorithmic Computer Science at Wroclaw University of Science and Technology.  

**Authors**:  
- Zofia Tarchalska  
- Anna Grelewska  

**Used Design Patterns**:  
- Mediator 
- Observer
- Singleton
- Single Responsibility Principle (SRP) 
---
## Functional Requirements for 1st Iteration
1. The system is based on a **client-server architecture**.
2. Players can connect to the server using a client application and join the game.
3. The system enforces the rules regarding the number of players (2, 3, 4, or 6 players).
4. Clients can send moves to other players via the server. The user interface is **console-based**, and moves are inputted as the starting and ending positions (Pattern: `move [0,16]x[0,24]->[0,16]x[0,24]`). 
5.  Verify whether the player's move is within the bounds of the board.
  
---
## How to run it:
### Clone the repository, and run the following commands in your terminal:
```bash
cd Game
mvn clean
mvn compile
```
### Run the server:
```bash
mvn exec:java -Dexec.mainClass="server.Server"
```
### Run the client in a separate terminal (each client requires its own terminal):
```bash
mvn exec:java -Dexec.mainClass="server.Client"
```
---
### Useful commands after connnecting to the server:
- `join`allows the client to join the game.
- `choose board (type e.g. big)` allows the client to select a board before starting the game (the game will not begin without this step).
- `game start` starts the game.
- `move [0,16]x[0,24]->[0,16]x[0,24]` allows the player to make a move and sends it to the other players.
- `bye` disconnects the client from the server.	
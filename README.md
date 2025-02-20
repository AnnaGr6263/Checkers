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
4. Clients can send moves to other players via the server. The user interface is **console-based**, 
    and moves are inputted as the starting and ending positions (Pattern: `move [0,16]x[0,24]->[0,16]x[0,24]`).
5. Verify whether the player's move is within the bounds of the board.
---
## Functional Requirements for 2nd Iteration
1. The gameplay logic was implemented, including at least two different **variants** for players (Classic Chineese Checkers and Yin And Yang variant).
2. A **graphical user interface (GUI)** for clients was developed using JavaFX.
3. Moves can be made by pressing the start field and then the end field (GUI).
4. Code correctness is continuously verified with **unit tests** written using JUnit.
5. A **class diagram** to illustrate the application's function
6. **Documentation** was generated using JavaDoc.
---
## Functional Requirements for 3rd Iteration
1. The game can have **bot** player joining, operating on the server. 
Create a sample bot implementation that makes at least **reasonably sensible moves**.
2. The Chinese Checkers game is extended to **support saving game data in a database** using the **Spring** framework. 
The **database** is able to record individual games and moves within each game. 
Additionally, the application is capable of **replaying a game** based on the saved data in the database.
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
##### Linux/MacOS
```bash
mvn exec:java -Dexec.mainClass="server.Client"
```
---
### Useful commands after connnecting to the server:
- `join`allows the client to join the game.
- `yes`switches the client to the Yin And Yang variant.
- `choose board (type e.g. big)` allows the client to select a board before starting the game (the game will not begin without this step).
- `game start` starts the game.
- `move [0,16]x[0,24]->[0,16]x[0,24]` allows the player to make a move and sends it to the other players.
- `skip` allows the player to skip their turn. All players are notified of this action.
- `bye` disconnects the client from the server.	

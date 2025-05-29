
# Communication Protocol – Technical Documentation

1. Introduction
2. System Architecture
3. Protocol Description
    1. Type of Protocol
    2. Message Structure
4. Protocol Extensibility
5. Complete Session Examples
6. Java Classes Used for Messages

## 1. Introduction

This document describes the communication protocol implemented for the Galaxy Trucker project, a digital version of the well-known board game, developed in Java.

Galaxy Trucker is a multiplayer game in which players compete by building and navigating their spaceships through eventful space journeys. To enable interaction between players, a client-server system based on the TCP protocol was developed, allowing reliable message exchange between game components.

The server centralizes the game logic, hosts the game model, the controller, and manages the overall game flow. The clients act as user interfaces through which players interact with the game and communicate with the server.

The goal of this document is to provide a detailed description of the communication structure between client and server, with particular focus on:

- the format and contents of exchanged messages,
- the sequence of interactions during different game phases,
- error handling,
- protocol extensibility.


## 2. System Architecture

The system follows a client-server architecture, where the server acts as the central coordinator of the game and manages the game logic, while the clients serve as user interfaces for the players.

Clients connect to the server via a TCP connection and do not communicate directly with each other. Each client interacts exclusively with the server, which receives player actions, processes them, and sends back updates about the game state.

### Main Components

**Server**

- GameModel: Contains the current game state and all objects needed to represent a match.
- Controller: Manages logic for actions sent by clients and updates the model accordingly.
- EventDispatcher: Notification system responsible for propagating game events to client interfaces.
- VirtualView: One per connected client, acts as an interface between the game model and the remote client.

**Client**

- Graphical Interface (JavaFX): Allows the user to interact with the game intuitively.
- VirtualServer: Manages the connection to the server and message sending/receiving.
- Controller: Coordinates logic between the graphical interface (JavaFX) and the VirtualServer. It handles user inputs, updates the view based on events received from the server, and translates user actions into messages to send to the server. It acts as a mediator between model and view, ensuring clear separation of concerns.

### Technologies Used

- Language: Java
- Communication Protocol: TCP
- Message Format: Serialized Java objects
- Multi-threading: Each client is managed by a dedicated thread on the server side
- GUI Libraries: JavaFX

## 3. Protocol Description

### 3.1 Type of Protocol

The system uses a TCP-based communication protocol implemented via Java Sockets. This choice ensures reliable and ordered data transmission, which is essential for proper synchronization between clients and server during gameplay.

Communication on the client side is asynchronous: each client can send multiple consecutive messages to the server without waiting for an immediate response, thanks to an input buffer on the server that handles incoming messages. This approach improves flexibility and responsiveness in the UI.

The server does not send unsolicited messages to clients, except for a heartbeat mechanism used to monitor active connections and detect potential disconnections.

The TCP connection remains open and persistent for the entire duration of the game session, from login to game end. If disconnected, the system supports client reconnection.

The protocol is based on the exchange of serialized Java objects representing the messages between the two parties.

### 3.2 Message Structure

All messages exchanged between client and server implement a common interface called GameMessage, which defines a method getType() to identify the message type. This structure supports extensibility and simplifies message handling on both server and client sides.

Messages are represented as Java objects serialized into JSON, using the Jackson library. This makes the protocol readable, interoperable, and easy to test.

Here are some key messages used in the protocol, categorized by game phase:

#### JoinRequestMessage

- Direction: Client → Server
- Purpose: Sends information about a player trying to join the lobby (e.g., name, color, avatar, readiness, admin role).
- Fields:
  - username: String – Player’s name
  - profilePicIds: int[] – Profile avatar identifiers (e.g., multiple images for a dynamic profile)
  - color: String – Chosen player color
  - admin: boolean – true if the player is the host
  - ready: boolean – true if the player marked themselves as “ready”
  - type: join_request

```json
{
   "type": "join_request",
   "username": "Marianna",
   "profilePicIds": [1, 4],
   "color": "RED",
   "admin": true,
   "ready": false
}
```

#### JoinResponseMessage

- Direction: Server → Client
- Purpose: Informs the client about the result of the lobby join request.
- Fields:
  - joinStatus: JoinStatus (enum: SUCCESS, LOBBY_FULL, NAME_TAKEN, GAME_STARTED)
  - type: join_response

```json
{
   "type": "join_response",
   "joinStatus": "SUCCESS"
}

```


#### GameStateMessage

- Direction: Server → Client
- Purpose: Sends the current game state to the client.
- Fields:
  - gameState: GameModel.GameState – Serialized game state object
  - type: game_state

```json
{
   "gameState": "BUILDING_SHIPS",
   "type": "game_state"
}
```

#### SimplePlayerMessage

- Direction: Variable (Server → Client / Client → Server)
- Purpose: Generic message used to notify events requiring only player identification. The type is dynamically set.
- Fields:
  - type: String – Message type (e.g., “ready_toggle”, “ping”)
  - playerName: String – Name of the player involved

```json
{
   "type": "ready_toggle",
   "playerName": "Marianna"
}

```

## 4. Protocol Extensibility

This section explains how the communication protocol is designed for extension, how it manages versioning, and how it behaves with unrecognized messages.

### Adding New Messages

The protocol is built to be easily extendable. All messages implement the GameMessage interface, which includes the getType() method. This allows dynamic message type identification during deserialization and dispatch.

To add a new message:

1. Create a new class implementing GameMessage.
2. Implement getType() with a unique identifier.
3. Update the dispatcher/event handler if necessary to handle the new type.

Thanks to Jackson’s serialization/deserialization capabilities, the system can dynamically instantiate new messages without modifying the core parsing logic.

### Behavior with Unknown Messages

The Jackson library is configured to ignore unknown fields using the annotation @JsonIgnoreProperties(ignoreUnknown = true). This makes the protocol tolerant to the addition of new fields in messages.

If a message type (getType()) is not recognized by the dispatcher, the message is ignored or discarded without errors, preserving system stability.

## 5. Complete Session Examples

Example of message exchange:

![img_1.png](img_1.png)

## 6. Java Classes Used for Messages

### sendMessage

Below is a concrete example of a method to serialize and send a message via TCP in JSON format:
```java
public void sendMessage(GameMessage message) {
    ObjectMapper mapper = new ObjectMapper();
    String json;
    try {
        json = mapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
    this.out.println(json);
}
```

This method serializes an object implementing GameMessage to JSON and sends it through an output stream (PrintWriter out).

- Extensibility: Works with any class implementing GameMessage.
- Compatibility: Uses JSON, a standard, readable, and manageable format.
- Robustness: Serialization errors are explicitly handled and raised.

#### Dependencies
```xml
<!-- Jackson -->
<dependencies>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.17.0</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.dataformat</groupId>
      <artifactId>jackson-dataformat-xml</artifactId>
      <version>2.18.2</version>
    </dependency>
</dependencies>
```

### VirtualView: run()

The run() method in VirtualView represents the main listening loop on the server side for a single client. It is executed on a separate thread for each connected client.

```java
public void run() {
    startPingLoop();

    try {
        while (true) {
            String msg = in.readLine();
            Logger.logDebug("RECEIVED: " + msg);
            if (msg == null) {
                Logger.logInfo("Client disconnected.");
                break;
            }
            this.controller.processCommand(this, msg);
        }

    } catch (SocketException e) {
        Logger.logWarning("Client disconnected: " + e.getMessage());
    } catch (IOException e) {
        if ("Stream closed".equals(e.getMessage())) {
            Logger.logInfo("Stream closed locally.");
        } else {
            Logger.logError("IOException: " + e.getMessage());
        }
    } finally {
        close();
    }
}
```

#### Functionality

- startPingLoop(): Starts a periodic mechanism (e.g., heartbeat) to verify the connection.
- in.readLine(): Waits for client messages (JSON as strings). This is a blocking call.
- processCommand(...): Forwards the message to the controller for processing.
- Exception Handling: Catches connection-related errors for both controlled and unexpected disconnections.

#### Lifecycle

1. A thread is launched for each client.
2. It enters an infinite loop listening on the input channel.
3. It terminates when:
   - the client disconnects (msg == null)
   - a socket exception occurs
   - the stream is closed
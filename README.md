# MiddlewareLab Chat Application

## Description
This project consists of a chat application implemented with two different architectures:

1. **Push Architecture** (functional):
    - Enables real-time communication between clients through a centralized server using a push-based approach.
    - Fully operational.

2. **Peer-to-Peer Architecture** (non-functional):
    - Implements direct communication between clients without relying on a centralized server.
    - Currently incomplete and not fully functional.

---

## Project Structure

The project is divided into the following packages:

### `peertopeer`
Contains the implementation of the Peer-to-Peer architecture.
- **Key Classes:**
    - `ChatServer`
    - `ClientManager`
    - `ClientManagerImpl`
    - `MessageBox`
    - `MessageBoxImpl`
    - `ServerConnection`
    - `ServerConnectionImpl`

### `push`
Contains the implementation of the Push architecture (functional).
- **Key Classes:**
    - `Server` (entry point for the server-side application)
    - `ChatClientUI` (entry point for client-side application)
    - `Connection`
    - `ConnectionImpl`
    - `Emitter`
    - `EmitterImpl`
    - `Receiver`
    - `ReceiverImpl`

---

## How to Run the Push Architecture (Functional Implementation)

1. **Start the Server:**
    - Navigate to the `push` package and run the `Server` class.

2. **Start the Clients:**
    - Launch two instances of the `ChatClientUI` class.

3. **Register and Login:**
    - For each client:
        - Register with a unique username & password.
        - Log in with the registered username & password.

4. **Chat Functionalities:**
    - Once logged in, clients can:
        - Send messages to other users.
        - Send messages to themselves.

---

## Peer-to-Peer Architecture (Non-Functional)

The `peertopeer` package contains the classes for a Peer-to-Peer chat implementation. This version is incomplete and may not work as expected. You are welcome to explore and contribute to its development.
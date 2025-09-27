# Java LAN Chat Application

A simple Java-based LAN chat application that allows multiple clients to communicate over a Local Area Network (LAN). The application consists of a **server** and a **Swing-based GUI client**. It uses Java sockets and multithreading to enable real-time chat functionality.

---

## Features

- Multi-client support with message broadcasting.
- Swing GUI for the client with chat display and message input.
- User can choose a display name.
- Simple commands: `/quit` to disconnect.
- Works over LAN; no internet required.

---

## Folder Structure

```
Java-LAN-Chat/
├── ChatServer.java
├── ChatClient.java
└── README.md
```

---

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Basic knowledge of running Java programs via terminal/command prompt

---

## How to Compile & Run

### 1. Compile

```bash
javac ChatServer.java ChatClient.java
```

### 2. Run Server

```bash
java ChatServer 12345
```
> Replace `12345` with any available port. If omitted, the default port `12345` will be used.

### 3. Run Client

```bash
java ChatClient
```

- Click **Connect** in the client GUI.
- Enter the server host (`localhost` for same machine or server IP on LAN).
- Enter the server port (`12345` by default).
- Enter your display name when prompted.

### 4. Chat

- Type messages in the input field and press **Send** or hit **Enter**.
- To exit, type `/quit`.

---

## Notes

- Ensure firewall allows the selected port for LAN connections.
- Server must be reachable on the network for clients to connect.
- The client GUI updates in real-time as messages arrive.

---

## Future Enhancements

- Private messaging between users.
- Chat rooms or channels.
- Persistent chat history.
- File transfer support.
- Encryption for secure messaging.

---

## References

- [Java Sockets Tutorial - GeeksforGeeks](https://www.geeksforgeeks.org/socket-programming-in-java/)  
- [Java Swing Tutorial - Oracle](https://docs.oracle.com/javase/tutorial/uiswing/)

---

## License

This project is open-source under the MIT License.


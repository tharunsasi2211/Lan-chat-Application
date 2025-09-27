import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Simple multi-client chat server.
 * Usage: java ChatServer [port]
 */
public class ChatServer {
    private final int port;
    // thread-safe set of client handlers
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("ChatServer started on port " + port);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler ch : clients) {
            if (ch != exclude) {
                ch.sendMessage(message);
            }
        }
    }

    private void remove(ClientHandler ch) {
        clients.remove(ch);
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private String name = "Anonymous";
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String msg) {
            if (out != null) {
                out.println(msg);
            }
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First line from client should be the username
                out.println("ENTER_NAME");
                String first = in.readLine();
                if (first != null && first.trim().length() > 0) {
                    name = first.trim();
                }
                broadcast(name + " has joined the chat", this);
                System.out.println(name + " connected: " + socket);

                String line;
                while ((line = in.readLine()) != null) {
                    // allow client to send "/quit" to disconnect
                    if (line.equalsIgnoreCase("/quit")) break;
                    String message = String.format("[%s] %s", name, line);
                    System.out.println("Broadcasting: " + message);
                    broadcast(message, this);
                }
            } catch (IOException e) {
                System.err.println("ClientHandler error: " + e.getMessage());
            } finally {
                try {
                    broadcast(name + " has left the chat", this);
                    remove(this);
                    socket.close();
                    System.out.println(name + " disconnected.");
                } catch (IOException ignored) {}
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 12345;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        ChatServer server = new ChatServer(port);
        server.start();
    }
}

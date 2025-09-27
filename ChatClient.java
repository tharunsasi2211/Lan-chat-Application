import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Simple Swing chat client.
 * Usage: java ChatClient [host] [port]
 */
public class ChatClient extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendBtn;
    private JButton connectBtn;
    private JTextField nameField;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread readerThread;

    public ChatClient() {
        super("Java Chat Client");
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendBtn = new JButton("Send");
        connectBtn = new JButton("Connect");
        nameField = new JTextField("User" + (int)(Math.random()*1000), 10);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Name:"));
        top.add(nameField);
        top.add(connectBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // actions
        connectBtn.addActionListener(e -> connect());
        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void connect() {
        if (socket != null && socket.isConnected()) {
            append("Already connected.");
            return;
        }
        String host = JOptionPane.showInputDialog(this, "Server host:", "localhost");
        if (host == null) return;
        String portStr = JOptionPane.showInputDialog(this, "Server port:", "12345");
        if (portStr == null) return;
        int port = Integer.parseInt(portStr);

        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Wait for server prompt for name
            String serverPrompt = in.readLine();
            if ("ENTER_NAME".equals(serverPrompt)) {
                out.println(nameField.getText().trim());
            } else {
                // if server doesn't use prompt, still send name
                out.println(nameField.getText().trim());
            }

            append("Connected to " + host + ":" + port);
            readerThread = new Thread(this::readLoop);
            readerThread.start();
        } catch (IOException ex) {
            append("Connection error: " + ex.getMessage());
        }
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                append(line);
            }
        } catch (IOException e) {
            append("Disconnected: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty() || out == null) return;
        out.println(text);
        // optionally show your own message immediately
        append("[Me] " + text);
        inputField.setText("");
        if (text.equalsIgnoreCase("/quit")) {
            closeConnection();
        }
    }

    private void append(String s) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(s + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        socket = null;
        in = null;
        out = null;
        append("Connection closed.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClient client = new ChatClient();
            client.setVisible(true);
            // optionally auto-connect if args provided
            if (args.length >= 1) {
                // not auto-connecting here to keep UI responsive; user can click Connect
            }
        });
    }
}

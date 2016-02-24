import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Server implements Runnable {
    private ServerSocket     server = null;
    private Thread           serverThread = null;
    private ServerThread     client = null;

    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Usage: java ChatServer port");
        else
            new Server(Integer.parseInt(args[0]));
    }

    public Server(int portNumber) {
        try {
            System.out.println("Binding to port " + portNumber + ", please wait  ...");
            server = new ServerSocket(portNumber);
            System.out.println("Server started: " + server);
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        if (serverThread == null) {
            serverThread = new Thread(this);
            serverThread.start();
        }
    }

    @Override
    public void run() {
        while (serverThread != null) {
            System.out.println("Waiting for a client to connect...");
            try {
                addThread(server.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addThread(Socket socket) throws IOException {
        System.out.println("Client accepted on socket: " + socket);
        client = new ServerThread(this, socket);
        client.open();
        client.start();
    }

    class ServerThread extends Thread
    {
        private Socket socket;
        private Server server;
        private int ID = -1;
        private DataInputStream dataIn;

        public ServerThread(Server server, Socket socket) {
           this.server = server;
           this.socket = socket;
           ID = socket.getPort();
        }

        public void run() {
            System.out.println("Server Thread " + ID + " running.");
            while (true) {
                try {
                    dataIn.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void open() throws IOException {
            dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        public void close() throws IOException {
            socket.close();
            dataIn.close();
        }
    }
}

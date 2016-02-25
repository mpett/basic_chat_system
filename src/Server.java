import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Server implements Runnable {
    private ServerSocket server;
    private Thread serverThread;
    private ArrayList<ServerThread> clientList = new ArrayList<ServerThread>();
    private final static String TERMINATE_MESSAGE = "end";

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

    private int findClient(int ID) {
        for (ServerThread client : clientList) {
            if (client.ID == ID)
                return clientList.indexOf(client);
        }
        return -1;
    }

    public synchronized void handleMessage(int ID, String inputMessage) {
        if (inputMessage.equals(TERMINATE_MESSAGE)) {
            ServerThread client = clientList.get(findClient(ID));
            client.sendMessage(TERMINATE_MESSAGE);
            killClient(ID);
        } else {
            for (ServerThread client : clientList)
                client.sendMessage(ID + " " + inputMessage);
        }
    }

    public synchronized void killClient(int ID) {
        int clientIndex = findClient(ID);
        if (clientIndex != -1) {
            ServerThread clientToKill = clientList.get(clientIndex);
            System.out.println("Removed client with ID: " + ID + " at index " + clientIndex);
            clientList.remove(clientIndex);
            try {
                clientToKill.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                addClient(server.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addClient(Socket socket) {
        System.out.println("Accepted new client at socket: " + socket);
        ServerThread newClient = new ServerThread(this, socket);
        clientList.add(newClient);
        try {
            newClient.open();
            newClient.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread extends Thread {
        private Server server;
        private Socket socket;
        private int ID = -1;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ServerThread(Server server, Socket socket) {
           super();
           this.server = server;
           this.socket = socket;
           ID = socket.getPort();
        }

        public void run() {
            System.out.println("Server Thread " + ID + " running.");
            while (true) {
                try {
                    server.handleMessage(ID, dataIn.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            try {
                dataOut.writeUTF(message);
                dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
                server.killClient(ID);
            }
        }

        public void open() throws IOException {
            dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }

        public void close() throws IOException {
            socket.close();
            dataIn.close();
            dataOut.close();
        }
    }
}

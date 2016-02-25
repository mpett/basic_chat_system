import java.io.*;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Client implements Runnable{
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private BufferedReader reader;
    private ClientThread clientThread;
    private Thread thread;

    private final static String TERMINATE_MESSAGE = "end";

    public static void main(String args[]) throws IOException {
        if (args.length != 2)
            System.out.println("Usage: java Client host port");
        else
            new Client(args[0], Integer.parseInt(args[1]));
    }

    public Client(String serverName, int portNumber) throws IOException {
        System.out.println("Establishing connection with server.");
        socket = new Socket(serverName, portNumber);
        System.out.println("Connected to server.");
        startClient();
    }

    public void run() {
        while (thread != null) {
            try {
                String transferredMessage = reader.readLine();
                dataOut.writeUTF(transferredMessage);
                dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleMessage(String message) throws IOException {
        if (message.equals(TERMINATE_MESSAGE)) {
            System.out.println("Press return to exit.");
            if (thread != null) {
                clientThread.closeThread();
            }
        }
        else System.out.println(message);
    }

    public void startClient() throws IOException {
        dataIn = new DataInputStream(socket.getInputStream());
        dataOut = new DataOutputStream(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(System.in));

        if (thread == null) {
            clientThread = new ClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    class ClientThread extends Thread {
        private Socket socket;
        private Client client;
        private DataInputStream dataIn;

        public ClientThread(Client client, Socket socket) throws IOException {
            this.client = client;
            this.socket = socket;
            openThread();
            start();
        }

        public void openThread() throws IOException {
            dataIn = new DataInputStream(socket.getInputStream());
        }

        public void closeThread() throws IOException {
            if (dataIn != null) dataIn.close();
        }

        public void run() {
            while (true) {
                try {
                    client.handleMessage(dataIn.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
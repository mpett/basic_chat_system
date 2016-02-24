import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by martinpettersson on 24/02/16.
 */
public class Server implements Runnable {
    private ServerSocket     server = null;
    private Thread           serverThread = null;
    private ServerThread clients[] = new ServerThread[50];
    private int clientCount = 0;

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

    private int findClient(int ID)
    {  for (int i = 0; i < clientCount; i++)
        if (clients[i].getID() == ID)
            return i;
        return -1;
    }

    public synchronized void handle(int ID, String input)
    {  if (input.equals(".bye"))
    {  clients[findClient(ID)].send(".bye");
        remove(ID); }
    else
        for (int i = 0; i < clientCount; i++)
            clients[i].send(ID + ": " + input);
    }

    public synchronized void remove(int ID)
    {  int pos = findClient(ID);
        if (pos >= 0)
        {  ServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            try
            {  toTerminate.close(); }
            catch(IOException ioe)
            {  //Print stack strace
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
                addThread(server.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addThread(Socket socket)
    {  if (clientCount < clients.length)
    {  System.out.println("Client accepted: " + socket);
        clients[clientCount] = new ServerThread(this, socket);
        try
        {  clients[clientCount].open();
            clients[clientCount].start();
            clientCount++; }
        catch(IOException ioe)
        {  System.out.println("Error opening thread: " + ioe); } }
    else
        System.out.println("Client refused: maximum " + clients.length + " reached.");
    }

    class ServerThread extends Thread
    {
        private Server server    = null;
        private Socket socket    = null;
        private int ID = -1;
        private DataInputStream dataIn =  null;
        private DataOutputStream dataOut = null;

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
                    server.handle(ID, dataIn.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(String msg)
        {   try
            {  dataOut.writeUTF(msg);
                dataOut.flush();
            }
            catch(IOException ioe)
            {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
                server.remove(ID);
            }
        }

        public int getID() {
            return ID;
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
